/*
    Copyright(c) 2021 AuroraLS3

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package com.djrapitops.extension;

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.ElementOrder;
import com.djrapitops.plan.extension.NotReadyException;
import com.djrapitops.plan.extension.annotation.DataBuilderProvider;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.TabInfo;
import com.djrapitops.plan.extension.builder.ExtensionDataBuilder;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * DataExtension.
 *
 * @author AuroraLS3
 */
@PluginInfo(name = "", iconName = "", iconFamily = Family.SOLID, color = Color.NONE)
@TabInfo(tab = "Skills", iconName = "book", elementOrder = ElementOrder.VALUES)
public class HeroesExtension implements DataExtension {

    public HeroesExtension() {
    }

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.PLAYER_JOIN,
                CallEvents.PLAYER_LEAVE
        };
    }

    @DataBuilderProvider
    public ExtensionDataBuilder playerData(UUID playerUUID) {
        Heroes heroes = Heroes.getInstance();
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) throw new NotReadyException();

        Hero hero = heroes.getCharacterManager().getHero(player);
        if (hero == null) throw new NotReadyException();

        Collection<Skill> skills = heroes.getSkillManager().getSkills();

        return newExtensionDataBuilder()
                .addTable("player_skills", createSkillTable(hero, skills), Color.BLUE, "Skills")
                .addValue(Long.class, valueBuilder("Hero level")
                        .icon(Icon.called("star").of(Color.BLUE).build())
                        .priority(100)
                        .showInPlayerTable()
                        .buildNumber(hero.getHeroLevel()))
                .addValue(Long.class, valueBuilder("Max mana")
                        .icon(Icon.called("hat-wizard").of(Color.LIGHT_BLUE).build())
                        .priority(90)
                        .buildNumber(hero.getMaxMana()))
                .addValue(Long.class, valueBuilder("Max shield")
                        .icon(Icon.called("shield-alt").of(Color.LIGHT_BLUE).build())
                        .priority(80)
                        .buildNumber(hero.getMaxShield()))
                .addValue(Long.class, valueBuilder("Max stamina")
                        .icon(Icon.called("running").of(Color.LIGHT_BLUE).build())
                        .priority(70)
                        .buildNumber(hero.getMaxStamina()))
                .addValue(Double.class, valueBuilder("Max equipment weight")
                        .icon(Icon.called("weight-hanging").of(Color.LIGHT_BLUE).build())
                        .priority(70)
                        .buildDouble(hero.getMaxEquipmentWeight()));
    }

    private Table createSkillTable(Hero hero, Collection<Skill> skills) {
        Table.Factory table = Table.builder()
                .columnOne("Skill", Icon.called("book").build())
                .columnTwo("Level", Icon.called("star").build());

        skills.stream()
                .map(skill -> {
                    String skillName = skill.getName();
                    OptionalInt level = hero.getHeroSkillLevel(skill);
                    if (!level.isPresent()) return null;
                    return new SkillLevel(skillName, level.getAsInt());
                }).filter(Objects::nonNull)
                .sorted()
                .forEach(skillLevel -> table.addRow(skillLevel.getSkillName(), skillLevel.getSkillName()));
        return table.build();
    }
}