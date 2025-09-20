package com.skthon.manjil.domain.reco.dto;

import java.util.List;

public record SessionBlock(List<String> warmup, List<String> cooldown) {}
