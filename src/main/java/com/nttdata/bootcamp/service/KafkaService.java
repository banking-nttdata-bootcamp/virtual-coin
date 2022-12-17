package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.VirtualCoin;

public interface KafkaService {
    void publish(VirtualCoin customer);
}
