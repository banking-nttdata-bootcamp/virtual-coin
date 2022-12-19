package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.VirtualCoin;
import com.nttdata.bootcamp.repository.VirtualCoinRepository;
import com.nttdata.bootcamp.service.KafkaService;
import com.nttdata.bootcamp.service.VirtualCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

//Service implementation
@Service
public class VirtualCoinServiceImpl implements VirtualCoinService {

    @Autowired
    private VirtualCoinRepository virtualCoinRepository;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public Flux<VirtualCoin> findAllVirtualCoin() {
        Flux<VirtualCoin> virtualCoinFlux = virtualCoinRepository
                .findAll();
        return virtualCoinFlux;
    }
    @Override
    public Mono<VirtualCoin> findVirtualCoinByCellNumber(String cellNumber) {
        Mono<VirtualCoin> virtualCoinFlux = virtualCoinRepository
                .findAll()
                .filter(x -> x.getCellNumber().equals(cellNumber) && x.getTypeOperation().equals("REGISTER") ).next();
        return virtualCoinFlux;
    }

    @Override
    public Flux<VirtualCoin> findVirtualCoinTransactionByCellNumber(String cellNumber) {
        Flux<VirtualCoin> virtualCoinFlux = virtualCoinRepository
                .findAll()
                .filter(x -> x.getCellNumber().equals(cellNumber) && !x.getTypeOperation().equals("REGISTER") );
        return virtualCoinFlux;
    }


    //save debit card
    //then main account is false by default
    @Override
    public Mono<VirtualCoin> saveVirtualCoin(VirtualCoin dataVirtualCoin) {
        /*Mono<VirtualCoin> virtualCoinMono = Mono.empty();

        virtualCoinMono = findVirtualCoinByCellNumber(dataVirtualCoin.getCellNumber())
                .flatMap(__ -> Mono.<VirtualCoin>error(new Error("The cell number do not have virtual coin")))
                .switchIfEmpty(virtualCoinRepository.save(dataVirtualCoin));
        return virtualCoinMono;*/
        return virtualCoinRepository.save(dataVirtualCoin);

    }



    @Override
    public Mono<VirtualCoin> saveTransactionVirtualCoin(VirtualCoin dataVirtualCoin) {
       /* Mono<VirtualCoin> virtualCoinMono = Mono.empty();
        return virtualCoinMono
                .flatMap(__ -> Mono.<VirtualCoin>error(new Error("This deposit number " + dataVirtualCoin.getCellNumber() + "exists")))
                .switchIfEmpty(saveTopic(dataVirtualCoin));*/
        return saveTopic(dataVirtualCoin);

    }

    //change main account of the debit card
   @Override
    public Mono<VirtualCoin> updateDebiCardVirtualCoin(VirtualCoin dataVirtualCoin) {
        Mono<VirtualCoin> virtualCoinMono = findVirtualCoinByCellNumber(dataVirtualCoin.getCellNumber());
        try{
            VirtualCoin virtualCoin = virtualCoinMono.block();
            virtualCoin.setFlagDebitCard(dataVirtualCoin.getFlagDebitCard());
            virtualCoin.setNumberDebitCard(dataVirtualCoin.getNumberDebitCard());
            virtualCoin.setModificationDate(dataVirtualCoin.getModificationDate());
            virtualCoin.setNumberAccount(dataVirtualCoin.getNumberAccount());
            return virtualCoinRepository.save(virtualCoin);
        }catch (Exception e){
            return Mono.<VirtualCoin>error(new Error("The virtual coin " + dataVirtualCoin.getDni() + " does not exists"));
        }
    }

    public Mono<VirtualCoin> saveTopic(VirtualCoin dataVirtual){
        Mono<VirtualCoin> monoVirtual = virtualCoinRepository.save(dataVirtual);
        this.kafkaService.publish(monoVirtual.block());
        return monoVirtual;
    }


}
