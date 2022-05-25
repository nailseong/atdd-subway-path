package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Name;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.fare.Fare;
import wooteco.subway.domain.fare.strategy.discount.BabyDiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.ChildDiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.DefaultDiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.DiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.DiscountStrategyFactory;
import wooteco.subway.domain.fare.strategy.discount.SeniorDiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.TeenagerDiscountStrategy;
import wooteco.subway.domain.fare.strategy.extrafare.ExtraFareStrategy;
import wooteco.subway.domain.fare.strategy.extrafare.ExtraFareStrategyFactory;
import wooteco.subway.domain.fare.strategy.extrafare.TenExtraFareStrategy;

class PathServiceTest extends ServiceTest {

    @InjectMocks
    private PathService pathService;

    @Mock
    private LineService lineService;

    @Mock
    private StationService stationService;

    @Mock
    private ExtraFareStrategyFactory extraFareStrategyFactory;

    @Mock
    private DiscountStrategyFactory discountStrategyFactory;

    private Station gangnam;
    private Station yeoksam;
    private Station seolleung;
    private Station samsung;
    private Station seoulForest;
    private Station wangsimni;
    private Station yacksu;
    private Station geumho;
    private Station oksu;

    private Line greenLine;
    private Line yellowLine;
    private Line orangeLine;

    @BeforeEach
    void setUpDate() {
        gangnam = new Station(1L, "강남역");
        yeoksam = new Station(2L, "역삼역");
        seolleung = new Station(3L, "선릉역");
        samsung = new Station(4L, "삼성역");

        seoulForest = new Station(5L, "서울숲역");
        wangsimni = new Station(6L, "왕십리역");

        yacksu = new Station(7L, "약수역");
        geumho = new Station(8L, "금호역");
        oksu = new Station(9L, "옥수역");

        greenLine = new Line(1L, new Name("2호선"), "green", 100);
        final Section greenSectionA = new Section(greenLine, gangnam, yeoksam, new Distance(10));
        final Section greenSectionB = new Section(greenLine, yeoksam, seolleung, new Distance(7));
        final Section greenSectionC = new Section(greenLine, seolleung, samsung, new Distance(11));
        greenLine = greenLine.addSections(new Sections(List.of(
                greenSectionA,
                greenSectionB,
                greenSectionC
        )));

        yellowLine = new Line(2L, new Name("수인분당선"), "yellow", 300);
        final Section yellowSectionA = new Section(yellowLine, seolleung, seoulForest, new Distance(3));
        final Section yellowSectionB = new Section(yellowLine, seoulForest, wangsimni, new Distance(8));
        yellowLine = yellowLine.addSections(new Sections(List.of(
                yellowSectionA,
                yellowSectionB
        )));

        orangeLine = new Line(3L, new Name("3호선"), "orange", 500);
        final Section orangeSectionA = new Section(orangeLine, yacksu, geumho, new Distance(12));
        final Section orangeSectionB = new Section(orangeLine, geumho, oksu, new Distance(6));
        orangeLine = orangeLine.addSections(new Sections(List.of(
                orangeSectionA,
                orangeSectionB
        )));
    }

    @Test
    @DisplayName("출발역과 도착역의 최단 경로에 포함된 역들을 조회한다.")
    void Find_Stations() {
        // given
        prepareBeforeFindPath(new DefaultDiscountStrategy());

        final List<Station> expected = List.of(
                gangnam,
                yeoksam,
                seolleung,
                seoulForest
        );

        // when
        final List<Station> actual = pathService.find(gangnam.getId(), seoulForest.getId(), 25)
                .getStations();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private void prepareBeforeFindPath(final DiscountStrategy discountStrategy) {
        given(stationService.findById(any(Long.class)))
                .willReturn(gangnam)
                .willReturn(seoulForest);

        final List<Line> lines = List.of(
                greenLine,
                yellowLine,
                orangeLine
        );
        given(lineService.findAll())
                .willReturn(lines);

        final ExtraFareStrategy extraFareStrategy = new TenExtraFareStrategy();
        given(extraFareStrategyFactory.findStrategyBy(any(Distance.class)))
                .willReturn(extraFareStrategy);

        given(discountStrategyFactory.findStrategyBy(any(Integer.class)))
                .willReturn(discountStrategy);
    }

    @ParameterizedTest
    @DisplayName("아기 승객의 출발역과 도착역의 최단 경로의 요금을 조회한다.")
    @ValueSource(ints = {1, 5})
    void Find_FareOfBaby(final int age) {
        // given
        prepareBeforeFindPath(new BabyDiscountStrategy());

        final Fare expected = Fare.from(-1250);

        // when
        final Fare actual = pathService.find(gangnam.getId(), seoulForest.getId(), age)
                .getFare();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("어린이 승객의 출발역과 도착역의 최단 경로의 요금을 조회한다.")
    @ValueSource(ints = {6, 12})
    void Find_FareOfChild(final int age) {
        // given
        prepareBeforeFindPath(new ChildDiscountStrategy());

        final Fare expected = Fare.from(-550);

        // when
        final Fare actual = pathService.find(gangnam.getId(), seoulForest.getId(), age)
                .getFare();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("청소년 승객의 출발역과 도착역의 최단 경로의 요금을 조회한다.")
    @ValueSource(ints = {13, 18})
    void Find_FareOfTeenager(final int age) {
        // given
        prepareBeforeFindPath(new TeenagerDiscountStrategy());

        final Fare expected = Fare.from(-130);

        // when
        final Fare actual = pathService.find(gangnam.getId(), seoulForest.getId(), age)
                .getFare();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("일반 승객의 출발역과 도착역의 최단 경로의 요금을 조회한다.")
    @ValueSource(ints = {19, 64})
    void Find_FareOfDefault(final int age) {
        // given
        prepareBeforeFindPath(new DefaultDiscountStrategy());

        final Fare expected = Fare.from(500);

        // when
        final Fare actual = pathService.find(gangnam.getId(), seoulForest.getId(), age)
                .getFare();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("노인 승객의 출발역과 도착역의 최단 경로의 요금을 조회한다.")
    void Find_FareOfSenior() {
        // given
        prepareBeforeFindPath(new SeniorDiscountStrategy());

        final Fare expected = Fare.from(-1250);

        // when
        final Fare actual = pathService.find(gangnam.getId(), seoulForest.getId(), 65)
                .getFare();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
