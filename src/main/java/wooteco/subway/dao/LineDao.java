package wooteco.subway.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.exception.notFound.EmptyResultException;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.LineAndSection;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Repository
public class LineDao {

    private SimpleJdbcInsert insertAction;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LineDao(DataSource dataSource) {
        this.insertAction = new SimpleJdbcInsert(dataSource)
            .withTableName("line")
            .usingGeneratedKeyColumns("id");
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Line insert(Line line) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", line.getId());
        params.put("name", line.getName());
        params.put("color", line.getColor());

        Long lineId = insertAction.executeAndReturnKey(params).longValue();
        return new Line(lineId, line.getName(), line.getColor());
    }

    public Line findById(Long id) {

        String sql = "select L.id as line_id, L.name as line_name, L.color as line_color, " +
            "S.id as section_id, S.distance as section_distance, " +
            "UST.id as up_station_id, UST.name as up_station_name, " +
            "DST.id as down_station_id, DST.name as down_station_name " +
            "from LINE L \n" +
            "left outer join SECTION S on L.id = S.line_id " +
            "left outer join STATION UST on S.up_station_id = UST.id " +
            "left outer join STATION DST on S.down_station_id = DST.id " +
            "WHERE L.id = :id";
        Map<String, Long> params = Collections.singletonMap("id", id);
        List<LineAndSection> result = namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
            new LineAndSection(
                rs.getLong("line_id"),
                rs.getString("line_name"),
                rs.getString("line_color"),
                new Section(
                    rs.getLong("section_id"),
                    new Station(rs.getLong("up_station_id"), rs.getString("up_station_name")),
                    new Station(rs.getLong("down_station_id"), rs.getString("down_station_name")),
                    rs.getInt("section_distance")
                )
            ));
        return mapLineFromLineAndSection(result);
    }

    public void update(Line newLine) {
        String sql = "update LINE set name = :name, color = :color where id = :id";
        SqlParameterSource params = new BeanPropertySqlParameterSource(newLine);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public List<Line> findAll() {
        String sql = "select L.id as line_id, L.name as line_name, L.color as line_color, " +
            "S.id as section_id, S.distance as section_distance, " +
            "UST.id as up_station_id, UST.name as up_station_name, " +
            "DST.id as down_station_id, DST.name as down_station_name " +
            "from LINE L \n" +
            "left outer join SECTION S on L.id = S.line_id " +
            "left outer join STATION UST on S.up_station_id = UST.id " +
            "left outer join STATION DST on S.down_station_id = DST.id ";
        List<Map<String, Object>> result = namedParameterJdbcTemplate
            .queryForList(sql, Collections.emptyMap());
        Map<Long, List<Map<String, Object>>> resultByLine = result.stream()
            .collect(Collectors.groupingBy(it -> (Long) it.get("line_id")));
        return resultByLine.entrySet().stream()
            .map(it -> mapLine(it.getValue()))
            .collect(Collectors.toList());
    }

    private Line mapLine(List<Map<String, Object>> result) {
        if (result.isEmpty()) {

            throw new EmptyResultException();
        }

        List<Section> sections = extractSections(result);

        return new Line(
            (Long) result.get(0).get("LINE_ID"),
            (String) result.get(0).get("LINE_NAME"),
            (String) result.get(0).get("LINE_COLOR"),
            new Sections(sections));
    }

    private Line mapLineFromLineAndSection(List<LineAndSection> result) {
        if (result.isEmpty()) {
            throw new EmptyResultException();
        }

        List<Section> sections = extractSectionsFromLineAndSection(result);

        return new Line(
            result.get(0).getId(),
            result.get(0).getName(),
            result.get(0).getColor(),
            new Sections(sections));
    }

    private List<Section> extractSections(List<Map<String, Object>> result) {
        if (result.isEmpty() || result.get(0).get("SECTION_ID") == null) {
            return Collections.EMPTY_LIST;
        }
        return result.stream()
            .collect(Collectors.groupingBy(it -> it.get("SECTION_ID")))
            .entrySet()
            .stream()
            .map(mappingResultToLine())
            .collect(Collectors.toList());
    }

    private List<Section> extractSectionsFromLineAndSection(List<LineAndSection> result) {
        if (result.isEmpty() || result.get(0).getId() == null) {
            return Collections.EMPTY_LIST;
        }
        return result.stream()
            .map(line -> line.getSection())
            .collect(Collectors.toList());
    }

    private Function<Entry<Object, List<Map<String, Object>>>, Section> mappingResultToLine() {
        return it ->
            new Section(
                (Long) it.getKey(),
                new Station((Long) it.getValue().get(0).get("UP_STATION_ID"),
                    (String) it.getValue().get(0).get("UP_STATION_Name")),
                new Station((Long) it.getValue().get(0).get("DOWN_STATION_ID"),
                    (String) it.getValue().get(0).get("DOWN_STATION_Name")),
                (int) it.getValue().get(0).get("SECTION_DISTANCE"));
    }

    public void deleteById(Long id) {
        Map<String, Long> params = Collections.singletonMap("id", id);
        namedParameterJdbcTemplate.update("delete from Line where id = :id", params);
    }
}