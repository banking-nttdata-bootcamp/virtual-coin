package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.VirtualCoin;
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

	//All DebitCard Registered
	@GetMapping("/findAllVirtualCoin")
	public Flux<VirtualCoin> findAllVirtualCoin() {
		Flux<VirtualCoin> virtualCoinFlux = virtualCoinService.findAllVirtualCoin();
		LOGGER.info("All virtual coin Registered: " + virtualCoinFlux);
		return virtualCoinFlux;
	}

	@GetMapping("/findAllDebitCardsByCustomer/{dni}")
	public Mono<VirtualCoin> findAllVirtualCoinByCustomer(@PathVariable("dni") String dni) {
		Mono<VirtualCoin> virtualCoinMono = virtualCoinService.findAllVirtualCoinByCustomer(dni);
		LOGGER.info("Virtual coin Registered by customer "+dni+": " + virtualCoinMono);
		return virtualCoinMono;
	}


	//Save Debit Card
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetDebitCard")
	@PostMapping(value = "/saveVirtualCoin")
	public Mono<VirtualCoin> saveVirtualCoin(@RequestBody VirtualCoinDto virtualCoinDto){

		VirtualCoin virtualCoin = new VirtualCoin();
		Mono.just(virtualCoin).doOnNext(t -> {
			t.setDni(virtualCoinDto.getDni());
			t.setEmail(virtualCoinDto.getEmail());
			t.setCellNumber(virtualCoinDto.getCellNumber());
			t.setIMEI(virtualCoinDto.getIMEI());
			t.setBalance(virtualCoinDto.getBalance());
			t.setCreationDate(new Date());
			t.setModificationDate(new Date());
		}).onErrorReturn(virtualCoin).onErrorResume(e -> Mono.just(virtualCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<VirtualCoin> passiveMono = virtualCoinService.saveVirtualCoin(virtualCoin);
		return passiveMono;
	}


	//Update main account of debit card
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetDebitCard")
	@PutMapping("/updateBalanceVirtualCoin/{dni}")
	public Mono<VirtualCoin> updateBalanceVirtualCoin(@PathVariable("dni") String dni, @PathVariable("balance") Double balance){

		VirtualCoin dataVirtualCoin = new VirtualCoin();
		Mono.just(dataVirtualCoin).doOnNext(t -> {
					t.setDni(dni);
					t.setBalance(balance);
					t.setModificationDate(new Date());
		}).onErrorReturn(dataVirtualCoin).onErrorResume(e -> Mono.just(dataVirtualCoin))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<VirtualCoin> updatePassive = virtualCoinService.updateBalanceVirtualCoin(dataVirtualCoin);
		return updatePassive;
	}
	@PutMapping("/updateBalanceVirtualCoin/{dni}")
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

	private Mono<VirtualCoin> fallBackGetDebitCard(Exception e){
		VirtualCoin virtualCoin = new VirtualCoin();
		Mono<VirtualCoin> staffMono= Mono.just(virtualCoin);
		return staffMono;
	}
}
