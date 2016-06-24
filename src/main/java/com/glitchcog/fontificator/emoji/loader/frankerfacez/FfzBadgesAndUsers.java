package com.glitchcog.fontificator.emoji.loader.frankerfacez;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Root badges and users model returned by the FrankerFaceZ badge API
 */
public class FfzBadgesAndUsers
{
    private List<Badge> badges = new ArrayList<Badge>();

    private Map<Integer, Set<String>> users = new TreeMap<Integer, Set<String>>();

    public List<Badge> getBadges()
    {
        return badges;
    }

    public void setBadges(List<Badge> badges)
    {
        this.badges = badges;
    }

    public Map<Integer, Set<String>> getUsers()
    {
        return users;
    }

    public void setUsers(Map<Integer, Set<String>> users)
    {
        this.users = users;
    }

}