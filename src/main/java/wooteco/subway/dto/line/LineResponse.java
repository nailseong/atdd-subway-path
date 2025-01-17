package wooteco.subway.dto.line;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationResponse;

public class LineResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final int extraFare;
    private final List<StationResponse> stations;

    public LineResponse(final Long id, final String name, final String color, final int extraFare,
                        final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
        this.stations = stations;
    }

    public static LineResponse of(final Line line, final List<Station> stations) {
        final List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getExtraFare(),
                stationResponses
        );
    }

    public static LineResponse from(final Line line) {
        final List<StationResponse> stations = line.getSections()
                .toStation()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getExtraFare(),
                stations
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getExtraFare() {
        return extraFare;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LineResponse that = (LineResponse) o;
        return extraFare == that.extraFare && Objects.equals(id, that.id) && Objects.equals(name,
                that.name) && Objects.equals(color, that.color) && Objects.equals(stations,
                that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, extraFare, stations);
    }

    @Override
    public String toString() {
        return "LineResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", extraFare=" + extraFare +
                ", stations=" + stations +
                '}';
    }
}
