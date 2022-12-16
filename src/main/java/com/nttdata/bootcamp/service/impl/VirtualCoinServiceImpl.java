package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.VirtualCoin;
import com.nttdata.bootcamp.repository.VirtualCoinRepository;
import com.nttdata.bootcamp.service.VirtualCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class VirtualCoinServiceImpl implements VirtualCoinService {

    @Autowired
    private VirtualCoinRepository virtualCoinRepository;

    @Override
    public Flux<VirtualCoin> findAllVirtualCoin() {
        Flux<VirtualCoin> virtualCoinFlux = virtualCoinRepository
                .findAll();
        return virtualCoinFlux;
    }
    @Override
    public Mono<VirtualCoin> findAllVirtualCoinByCustomer(String dni) {
        Mono<VirtualCoin> virtualCoinFlux = virtualCoinRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni)).next();
        return virtualCoinFlux;
    }


    //save debit card
    //then main account is false by default
    @Override
    public Mono<VirtualCoin> saveVirtualCoin(VirtualCoin dataVirtualCoin) {
        Mono<VirtualCoin> virtualCoinMono = Mono.empty();

        virtualCoinMono = findAllVirtualCoinByCustomer(dataVirtualCoin.getDni())
                .flatMap(__ -> Mono.<VirtualCoin>error(new Error("The customer don have virtual coin")))
                .switchIfEmpty(virtualCoinRepository.save(dataVirtualCoin));
        return virtualCoinMono;

    }
    //change main account of the debit card
    @Override
    public Mono<VirtualCoin> updateBalanceVirtualCoin(VirtualCoin dataVirtualCoin) {
        Mono<VirtualCoin> virtualCoinMono = findAllVirtualCoinByCustomer(dataVirtualCoin.getDni());
        try{
            VirtualCoin virtualCoin = virtualCoinMono.block();
            virtualCoin.setBalance(dataVirtualCoin.getBalance());
            virtualCoin.setModificationDate(dataVirtualCoin.getModificationDate());
            return virtualCoinRepository.save(virtualCoin);
        }catch (Exception e){
            return Mono.<VirtualCoin>error(new Error("The virtual coin " + dataVirtualCoin.getDni() + " does not exists"));
        }
    }@Override
    public Mono<VirtualCoin> updateDebiCardVirtualCoin(VirtualCoin dataVirtualCoin) {
        Mono<VirtualCoin> virtualCoinMono = findAllVirtualCoinByCustomer(dataVirtualCoin.getDni());
        try{
            VirtualCoin virtualCoin = virtualCoinMono.block();
            virtualCoin.setFlagDebitCard(dataVirtualCoin.getFlagDebitCard());
            virtualCoin.setNumberDebitCard(dataVirtualCoin.getNumberDebitCard());
            virtualCoin.setModificationDate(dataVirtualCoin.getModificationDate());
            return virtualCoinRepository.save(virtualCoin);
        }catch (Exception e){
            return Mono.<VirtualCoin>error(new Error("The virtual coin " + dataVirtualCoin.getDni() + " does not exists"));
        }
    }


}
