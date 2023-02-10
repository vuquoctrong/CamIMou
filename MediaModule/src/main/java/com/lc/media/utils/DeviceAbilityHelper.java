package com.lc.media.utils;

public class DeviceAbilityHelper {


    public static boolean isHasAbility(String deviceAbility, String channelAbility, String ability1, String ability2) {
        if (deviceAbility == null || channelAbility == null) return false;

        if (deviceAbility.contains(ability1) || deviceAbility.contains(ability2)) {
            return true;
        } else if (channelAbility.contains(ability1) || channelAbility.contains(ability2)) {
            return true;
        }

        return false;

    }

}
