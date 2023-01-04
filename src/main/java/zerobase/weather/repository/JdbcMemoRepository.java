package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    // jdbc 를 활용하기 위해 생성
    private final JdbcTemplate jdbcTemplate;

    // 자동으로 정보를 가져옴
    @Autowired
    // application.properties 에 입력했던 정보를 불러와서 datasource 에 정보를 담음
    public JdbcMemoRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // DB 에 데이터를 저장하기 위해
    public Memo save(Memo memo) {
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    public List<Memo> findAll() {
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    public Optional<Memo> findById(int id) {
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();

    }

    private RowMapper<Memo> memoRowMapper() {
        // DB 에서 가져오는 데이터 형식은 ResultSet 이므로 저장하기 위해 바꿔줘야함
        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
                );
    }
}
