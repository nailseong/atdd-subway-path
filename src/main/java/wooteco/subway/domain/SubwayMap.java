package wooteco.subway.domain;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.path.NoSuchPathException;

public class SubwayMap {

    private final DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPathMap;

    public SubwayMap(final Sections sections) {
        this.shortestPathMap = toShortestPath(sections);
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> toShortestPath(final Sections sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for (Section section : sections.getValue()) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
        return new DijkstraShortestPath<>(graph);
    }

    public List<Station> searchPath(final Station sourceStation, final Station targetStation) {
        final GraphPath<Station, DefaultWeightedEdge> shortestPath = searchShortestPath(sourceStation, targetStation);
        return shortestPath.getVertexList();
    }

    private GraphPath<Station, DefaultWeightedEdge> searchShortestPath(final Station sourceStation,
                                                                       final Station targetStation) {
        validateStations(sourceStation, targetStation);

        final GraphPath<Station, DefaultWeightedEdge> shortestPath = shortestPathMap.getPath(sourceStation,
                targetStation);

        if (shortestPath == null) {
            throw new NoSuchPathException();
        }
        return shortestPath;
    }

    private void validateStations(final Station sourceStation, final Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new IllegalInputException("출발역과 도착역이 동일합니다.");
        }
    }

    public Distance searchDistance(final Station sourceStation, final Station targetStation) {
        final GraphPath<Station, DefaultWeightedEdge> shortestPath = searchShortestPath(sourceStation, targetStation);
        return new Distance((int) shortestPath.getWeight());
    }
}