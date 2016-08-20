package com.quadrastats.screens.stats;

import com.quadrastats.screens.StaticRiotData;

import java.util.List;
import java.util.Map;

class WinRatePackage {
    List<String> names;
    String selectedRole;
    StaticRiotData staticRiotData;
    List<WinRate> winRates;
    Map<String, Map<String, Map<String, WinRate>>> winRatesBySumChamp;
}
