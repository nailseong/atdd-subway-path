package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.subwaymap.SubwayMap;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.path.NoSuchPathException;

class SubwayMapTest {

    private SubwayMap subwayMap;
    private Station gangnam;
    private Station yeoksam;
    private Station seolleung;
    private Station samsung;
    private Station seoulForest;
    private Station wangsimni;
    private Station yacksu;
    private Station geumho;
    private Station oksu;

    @BeforeEach
    void setUpData() {
        gangnam = new Station(1L, "강남역");
        yeoksam = new Station(2L, "역삼역");
        seolleung = new Station(3L, "선릉역");
        samsung = new Station(4L, "삼성역");

        seoulForest = new Station(5L, "서울숲역");
        wangsimni = new Station(6L, "왕십리역");

        yacksu = new Station(7L, "약수역");
        geumho = new Station(8L, "금호역");
        oksu = new Station(9L, "옥수역");

        Line greenLine = new Line(1L, new Name("2호선"), "green", 100);
        greenLine = greenLine.addSections(new Sections(List.of(
                new Section(greenLine, gangnam, yeoksam, new Distance(10)),
                new Section(greenLine, yeoksam, seolleung, new Distance(7)),
                new Section(greenLine, seolleung, samsung, new Distance(11))
        )));

        Line yellowLine = new Line(2L, new Name("수인분당선"), "yellow", 300);
        yellowLine = yellowLine.addSections(new Sections(List.of(
                new Section(yellowLine, seolleung, seoulForest, new Distance(3)),
                new Section(yellowLine, seoulForest, wangsimni, new Distance(8))
        )));

        Line orangeLine = new Line(2L, new Name("3호선"), "orange", 400);
        orangeLine = orangeLine.addSections(new Sections(List.of(
                new Section(orangeLine, yacksu, geumho, new Distance(12)),
                new Section(orangeLine, geumho, oksu, new Distance(6))
        )));

        subwayMap = new SubwayMap(List.of(greenLine, yellowLine, orangeLine));
    }

    @Test
    @DisplayName("출발역과 도착익이 동일한 경로를 조회할 경우 예외를 던진다.")
    void SearchPath_SameStations_ExceptionThrown() {
        assertThatThrownBy(() -> subwayMap.searchPath(gangnam, gangnam))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("출발역과 도착역이 동일합니다.");
    }

    @Test
    @DisplayName("경로를 찾을 수 없는 경우 예외를 던진다.")
    void SearchPath_InvalidPath_ExceptionThrown() {
        assertThatThrownBy(() -> subwayMap.searchPath(gangnam, oksu))
                .isInstanceOf(NoSuchPathException.class);
    }

    @Test
    @DisplayName("출발역에서 도착역의 최단 경로를 탐색한다. (강남역 -> 서울숲역)")
    void SearchPath() {
        // given
        final List<Station> expected = List.of(
                gangnam,
                yeoksam,
                seolleung,
                seoulForest
        );

        // when
        final List<Station> actual = subwayMap.searchPath(gangnam, seoulForest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("출발역에서 도착역의 최단 경로의 거리를 계산한다. (강남역 -> 서울숲역)")
    void SearchDistance() {
        // given
        Distance expected = new Distance(20);

        // when
        Distance actual = subwayMap.searchDistance(gangnam, seoulForest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("출발역에서 도착역의 최단 경로의 노선 중 최대 추가 요금을 계산한다.")
    void CalculateMaxExtraFare() {
        // given

        // when
        final int actual = subwayMap.calculateMaxExtraFare(gangnam, seoulForest);

        // then
        assertThat(actual).isEqualTo(300);
    }
}
