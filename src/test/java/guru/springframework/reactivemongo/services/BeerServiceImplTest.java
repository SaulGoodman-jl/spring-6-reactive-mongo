package guru.springframework.reactivemongo.services;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.mapper.BeerMapper;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.repositories.BeerRepository;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@SpringBootTest
public class BeerServiceImplTest {

    @Autowired
    BeerService beerService;

    @Autowired
    BeerMapper beerMapper;

    BeerDTO beerDTO;
    @Autowired
    private BeerRepository beerRepository;

    @BeforeEach
    void setUp() {
        beerDTO = beerMapper.beerToBeerDto(getTestBeer());
    }

    @Test
    void testFindByBeerStyle() {
        BeerDTO savedBeerDto = getSavedBeerDto();
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerService.findByBeerStyle(savedBeerDto.getBeerStyle())
                .subscribe(dto -> {
                    System.out.println(dto);
                    atomicBoolean.set(true);
                });

        Awaitility.await().untilTrue(atomicBoolean);
    }

    @Test
    void testFindFirstByBeerName() {

        AtomicReference<BeerDTO> atomicReference = new AtomicReference<>();

        BeerDTO savedBeerDto = getSavedBeerDto();

        beerRepository.findFirstByBeerName(savedBeerDto.getBeerName()).subscribe(foundBeer -> {
            atomicReference.set(beerMapper.beerToBeerDto(foundBeer));
        });

        Awaitility.await().until(() -> atomicReference.get() != null);

        System.out.println(atomicReference.get());
    }

    @Test
    void saveBeerUseSubscriber() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<BeerDTO> atomicDto = new AtomicReference<>();

        Mono<BeerDTO> savedMono = beerService.saveBeer(Mono.just(beerDTO));

        savedMono.subscribe(savedDto -> {
            System.out.println(savedDto.toString());
            atomicBoolean.set(true);
            atomicDto.set(savedDto);
        });

        Awaitility.await().untilTrue(atomicBoolean);

        BeerDTO persistedDto = atomicDto.get();
        Assertions.assertThat(persistedDto).isNotNull();
        Assertions.assertThat(persistedDto.getId()).isNotNull();
    }

    @Test
    void testSaveBeerUseBlock() {
        BeerDTO savedDto = beerService.saveBeer(Mono.just(beerDTO)).block();
        Assertions.assertThat(savedDto).isNotNull();
        Assertions.assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    void testUpdateBeerUseSubscriber() {
        AtomicReference<BeerDTO> atomicDto = new AtomicReference<>();

        BeerDTO savedBeerDto = getSavedBeerDto();
        final String newName = "New Beer Name With Subscriber";
        savedBeerDto.setBeerName(newName);
        beerService.updateBeer(savedBeerDto.getId(), savedBeerDto).subscribe(updatedDto -> {
            beerService.getById(updatedDto.getId()).subscribe(atomicDto::set);
        });

        Awaitility.await().until(() -> atomicDto.get() != null);

        Assertions.assertThat(atomicDto.get().getBeerName()).isEqualTo(newName);
    }

    @Test
    void testUpdateBeerUseBlock() {

        BeerDTO savedBeerDto = getSavedBeerDto();
        final String newName = "New Beer Name With Block";
        savedBeerDto.setBeerName(newName);
        BeerDTO updatedDto = beerService.updateBeer(savedBeerDto.getId(), savedBeerDto).block();

        BeerDTO fetchedDto = beerService.getById(updatedDto.getId()).block();
        Assertions.assertThat(fetchedDto.getBeerName()).isEqualTo(newName);
    }

    @Test
    void testDeleteBeer() {
        BeerDTO beerToDelete = getSavedBeerDto();

        beerService.deleteBeerById(beerToDelete.getId()).block();

        BeerDTO emptyBeer = beerService.getById(beerToDelete.getId()).block();

        Assertions.assertThat(emptyBeer).isNull();
    }

    public BeerDTO getSavedBeerDto() {
        return beerService.saveBeer(beerMapper.beerToBeerDto(getTestBeer())).block();
    }

    public static Beer getTestBeer() {
        return Beer.builder()
                .beerName("Space Dust")
                .beerStyle("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123213")
                .build();
    }
}