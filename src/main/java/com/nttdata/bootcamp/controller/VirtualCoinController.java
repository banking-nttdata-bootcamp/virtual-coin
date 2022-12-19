package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.VirtualCoin;
import com.nttdata.bootcamp.entity.dto.UpdateVirtualCoinDto;
import com.nttdata.bootcamp.entity.dto.VirtualCoinTransactionDto;
import com.nttdata.bootcamp.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.VirtualCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import com.nttdata.bootcamp.entity.dto.VirtualCoinDto;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/virtual-coin")
public class VirtualCoinController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VirtualCoinController.class);
	@Autowired
	private VirtualCoinService virtualCoinService;

	//All Virtual-coin Registered
	@GetMapping("/findAllVirtualCoin")
	public Flux<VirtualCoin> findAllVirtualCoin() {
		Flux<VirtualCoin> virtualCoinFlux = virtualCoinService.findAllVirtualCoin();
		LOGGER.info("All virtual coin Registered: " + virtualCoinFlux);
		return virtualCoinFlux;
	}

	//Virtual-coin registered  by customer

	@GetMapping("/findVirtualCoinByCellNumber/{cellNumber}")
	public Mono<VirtualCoin> findVirtualCoinByCellNumber(@PathVariable("cellNumber") String cellNumber) {
		Mono<VirtualCoin> virtualCoinMono = virtualCoinService.findVirtualCoinByCellNumber(cellNumber);
		LOGGER.info("Virtual coin Registered by cell number "+cellNumber+": " + virtualCoinMono);
		return virtualCoinMono;
	}

	@GetMapping("/findTransactionsByCellNumber/{cellNumber}")
	public Flux<VirtualCoin> findTransactionsByCellNumber(@PathVariable("cellNumber") String cellNumber) {
		Flux<VirtualCoin> virtualCoinFlux = virtualCoinService.findVirtualCoinTransactionByCellNumber(cellNumber);
		LOGGER.info("Virtual coin transaction Registered by cell number "+cellNumber+": " + virtualCoinFlux);
		return virtualCoinFlux;
	}
	//Save Virtual-coin
	@PostMapping(value = "/saveVirtualCoin")
	public Mono<VirtualCoin> saveVirtualCoin(@RequestBody VirtualCoinDto virtualCoinDto){

		VirtualCoin virtualCoin = new VirtualCoin();
		Mono.just(virtualCoin).doOnNext(t -> {
			t.setDni(virtualCoinDto.getDni());
			t.setEmail(virtualCoinDto.getEmail());
			t.setCellNumber(virtualCoinDto.getCellNumber());
			t.setIMEI(virtualCoinDto.getIMEI());
			t.setNumberAccount("");
			t.setMount(0.00);
			t.setFlagDebitCard(false);
			t.setNumberDebitCard("");
			t.setTypeOperation("REGISTER");
			t.setCreationDate(new Date());
			t.setModificationDate(new Date());
		}).onErrorReturn(virtualCoin).onErrorResume(e -> Mono.just(virtualCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<VirtualCoin> passiveMono = virtualCoinService.saveVirtualCoin(virtualCoin);
		return passiveMono;
	}
	// save the send of a payment
	@PostMapping(value = "/sendPayment")
	public Mono<VirtualCoin> sendPayment(@RequestBody VirtualCoinTransactionDto transactionDto){
		Mono<VirtualCoin> virtualCoinMono = virtualCoinService.findVirtualCoinByCellNumber(transactionDto.getCellNumber());
		VirtualCoin virtualCoin = new VirtualCoin();
		Mono.just(virtualCoin).doOnNext(t -> {
					t.setDni(virtualCoinMono.block().getDni());
					t.setCellNumber(virtualCoinMono.block().getCellNumber());
					t.setMount(transactionDto.getMount()*-1);
					t.setIMEI(virtualCoinMono.block().getIMEI());
					t.setEmail(virtualCoinMono.block().getEmail());
					t.setFlagDebitCard(virtualCoinMono.block().getFlagDebitCard());
					t.setNumberDebitCard(virtualCoinMono.block().getNumberDebitCard());
					t.setNumberAccount(virtualCoinMono.block().getNumberAccount());
					t.setTypeOperation("SEND");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(virtualCoin).onErrorResume(e -> Mono.just(virtualCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<VirtualCoin> passiveMono = virtualCoinService.saveTransactionVirtualCoin(virtualCoin);
		return passiveMono;
	}
	//save get of a paid
	@PostMapping(value = "/getPaid")
	public Mono<VirtualCoin> getPaid(@RequestBody VirtualCoinTransactionDto transactionDto){
		Mono<VirtualCoin> virtualCoinMono = virtualCoinService.findVirtualCoinByCellNumber(transactionDto.getCellNumber());
		VirtualCoin virtualCoin = new VirtualCoin();
		Mono.just(virtualCoin).doOnNext(t -> {
					t.setDni(virtualCoinMono.block().getDni());
					t.setCellNumber(transactionDto.getCellNumber());
					t.setMount(transactionDto.getMount());
					t.setIMEI(virtualCoinMono.block().getIMEI());
					t.setEmail(virtualCoinMono.block().getEmail());
					t.setFlagDebitCard(virtualCoinMono.block().getFlagDebitCard());
					t.setNumberDebitCard(virtualCoinMono.block().getNumberDebitCard());
					t.setNumberAccount(virtualCoinMono.block().getNumberAccount());
					t.setTypeOperation("GET");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(virtualCoin).onErrorResume(e -> Mono.just(virtualCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<VirtualCoin> passiveMono = virtualCoinService.saveTransactionVirtualCoin(virtualCoin);
		return passiveMono;
	}


	// update debit card of then Virtual-coin
	@PutMapping("/updateDebiCardVirtualCoin")
	public Mono<VirtualCoin> updateDebiCardVirtualCoin(@RequestBody UpdateVirtualCoinDto virtualCoinDto){
		VirtualCoin dataVirtualCoin = new VirtualCoin();
		Mono.just(dataVirtualCoin).doOnNext(t -> {
					t.setCellNumber(virtualCoinDto.getCellNumber());
					t.setFlagDebitCard(true);
					t.setNumberDebitCard(virtualCoinDto.getDebitCard());
					t.setNumberAccount(virtualCoinDto.getNumberAccount());
					t.setModificationDate(new Date());
				}).onErrorReturn(dataVirtualCoin).onErrorResume(e -> Mono.just(dataVirtualCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<VirtualCoin> updatePassive = virtualCoinService.updateDebiCardVirtualCoin(dataVirtualCoin);
		return updatePassive;
	}


}
