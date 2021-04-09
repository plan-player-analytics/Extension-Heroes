package com.djrapitops.extension;

import java.util.Objects;

public class SkillLevel implements Comparable<SkillLevel> {
    private final String skillName;
    private final int level;

    public SkillLevel(String skillName, int level) {
        this.skillName = skillName;
        this.level = level;
    }

    public String getSkillName() {
        return skillName;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public int compareTo(SkillLevel other) {
        return Integer.compare(other.level, this.level);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillLevel that = (SkillLevel) o;
        return getLevel() == that.getLevel() && Objects.equals(getSkillName(), that.getSkillName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSkillName(), getLevel());
    }
}
