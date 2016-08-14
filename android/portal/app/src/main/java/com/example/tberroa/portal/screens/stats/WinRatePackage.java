package com.example.tberroa.portal.screens.stats;

import com.example.tberroa.portal.screens.StaticRiotData;

import java.util.List;
import java.util.Map;

class WinRatePackage {
    List<String> names;
    String selectedRole;
    StaticRiotData staticRiotData;
    List<WinRate> winRates;
    Map<String, Map<String, Map<String, WinRate>>> winRatesBySumChamp;
}
