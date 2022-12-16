package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.VirtualCoin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface VirtualCoinService {
    Flux<VirtualCoin> findAllVirtualCoin();
    Mono<VirtualCoin> findAllVirtualCoinByCustomer(String dni);

    Mono<VirtualCoin> saveVirtualCoin(VirtualCoin dataVirtualCoin);

    Mono<VirtualCoin> updateBalanceVirtualCoin(VirtualCoin dataVirtualCoin);

    Mono<VirtualCoin> updateDebiCardVirtualCoin(VirtualCoin dataVirtualCoin);

}
