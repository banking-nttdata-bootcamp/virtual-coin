package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.VirtualCoin;
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

	@GetMapping("/findVirtualCoinByCellNumber/{dni}")
	public Mono<VirtualCoin> findVirtualCoinByCellNumber(@PathVariable("cellNumber") String cellNumber) {
		Mono<VirtualCoin> virtualCoinMono = virtualCoinService.findVirtualCoinByCellNumber(cellNumber);
		LOGGER.info("Virtual coin Registered by cell number "+cellNumber+": " + virtualCoinMono);
		return virtualCoinMono;
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

	@PostMapping(value = "/sendPayment")
	public Mono<VirtualCoin> sendPayment(@RequestBody VirtualCoinTransactionDto transactionDto){
		Mono<VirtualCoin> virtualCoinMono = virtualCoinService.findVirtualCoinByCellNumber(transactionDto.getCellNumber());
		VirtualCoin virtualCoin = new VirtualCoin();
		Mono.just(virtualCoin).doOnNext(t -> {
					t.setDni(virtualCoinMono.block().getDni());
					t.setCellNumber(transactionDto.getCellNumber());
					t.setMount(transactionDto.getMount()*-1);
					t.setFlagDebitCard(virtualCoinMono.block().getFlagDebitCard());
					t.setNumberDebitCard(virtualCoinMono.block().getNumberDebitCard());

				}).onErrorReturn(virtualCoin).onErrorResume(e -> Mono.just(virtualCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<VirtualCoin> passiveMono = virtualCoinService.saveTransactionVirtualCoin(virtualCoin);
		return passiveMono;
	}
	@PostMapping(value = "/getPaid")
	public Mono<VirtualCoin> getPaid(@RequestBody VirtualCoinTransactionDto transactionDto){
		Mono<VirtualCoin> virtualCoinMono = virtualCoinService.findVirtualCoinByCellNumber(transactionDto.getCellNumber());
		VirtualCoin virtualCoin = new VirtualCoin();
		Mono.just(virtualCoin).doOnNext(t -> {
					t.setDni(virtualCoinMono.block().getDni());
					t.setCellNumber(transactionDto.getCellNumber());
					t.setMount(transactionDto.getMount());
					t.setFlagDebitCard(virtualCoinMono.block().getFlagDebitCard());
					t.setNumberDebitCard(virtualCoinMono.block().getNumberDebitCard());

				}).onErrorReturn(virtualCoin).onErrorResume(e -> Mono.just(virtualCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<VirtualCoin> passiveMono = virtualCoinService.saveTransactionVirtualCoin(virtualCoin);
		return passiveMono;
	}


	// update debit card of then Virtual-coin
	@PutMapping("/updateDebiCardVirtualCoin/{dni}")
	public Mono<VirtualCoin> updateDebiCardVirtualCoin(@PathVariable("dni") String dni){
		Boolean flagdebitcard= true;
		String debicard= "";
		VirtualCoin dataVirtualCoin = new VirtualCoin();
		Mono.just(dataVirtualCoin).doOnNext(t -> {
					t.setDni(dni);
					if(flagdebitcard) {
						t.setFlagDebitCard(true);
						t.setNumberDebitCard(debicard);
					}else{
						t.setFlagDebitCard(false);
						t.setNumberDebitCard("");
					}
					t.setModificationDate(new Date());
				}).onErrorReturn(dataVirtualCoin).onErrorResume(e -> Mono.just(dataVirtualCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<VirtualCoin> updatePassive = virtualCoinService.updateDebiCardVirtualCoin(dataVirtualCoin);
		return updatePassive;
	}


}
