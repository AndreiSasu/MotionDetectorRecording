package com.asasu.motiondetect.entity.file;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.asasu.motiondetect.interfaces.IFileSaverDao;
import org.springframework.stereotype.Component;

@Component
public class FileSaverDao implements IFileSaverDao {
	private Log log = LogFactory.getLog(FileSaverDao.class);

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	private InsertFileSaver insertFileSaver;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final class InsertFileSaver extends SqlUpdate {
		private static final String SQL_INSERT_PF = "insert into file_savers (file_saver_name, credential_token) values (:file_saver_name,:credential_token)";

		public InsertFileSaver(DataSource dataSource) {
			super(dataSource, SQL_INSERT_PF);
			super.declareParameter(new SqlParameter("file_saver_name",
					Types.VARCHAR));
			super.declareParameter(new SqlParameter("credential_token",
					Types.VARCHAR));
			super.setGeneratedKeysColumnNames(new String[] { "id" });
			super.setReturnGeneratedKeys(true);
		}
	}

	@Override
	public FileSaver insert(FileSaver fs) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("file_saver_name", fs.getFileSaverName());
		paramMap.put("credential_token", fs.getCredentialToken());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		insertFileSaver.updateByNamedParam(paramMap, keyHolder);
		fs.setId(keyHolder.getKey().longValue());
		log.debug("File saver inserted with id: " + fs.getId());
		return fs;
	}

	@Override
	public void update(FileSaver fs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(FileSaver fs) {
		// TODO Auto-generated method stub

	}

	@Override
	public FileSaver findByName(String fsName) {
		String sql = "select id, file_saver_name, credential_token from file_savers where file_saver_name =:file_saver_name";
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("file_saver_name", fsName);
		List<FileSaver> fs = namedParameterJdbcTemplate.query(sql,
				namedParameters, new RowMapper<FileSaver>() {
			@Override
			public FileSaver mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				FileSaver fs = new FileSaver();
				fs.setId(rs.getLong("id"));
				fs.setCredentialToken(rs.getString("credential_token"));
				fs.setFileSaverName(rs.getString("file_saver_name"));
				return fs;
			}
		});
		if (!fs.isEmpty()) {
			return fs.get(0);
		} else if (fs.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, fs.size());
		}
		return null;
	}

	@Override
	public FileSaver findByToken(String token) {
		String sql = "select id, file_saver_name, credential_token from file_savers where credential_token =:credential_token";
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("credential_token", token);
		List<FileSaver> fs = namedParameterJdbcTemplate.query(sql,
				namedParameters, new RowMapper<FileSaver>() {
			@Override
			public FileSaver mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				FileSaver fs = new FileSaver();
				fs.setId(rs.getLong("id"));
				fs.setCredentialToken(rs.getString("credential_token"));
				fs.setFileSaverName(rs.getString("file_saver_name"));
				return fs;
			}
		});
		if (!fs.isEmpty()) {
			return fs.get(0);
		} else if (fs.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, fs.size());
		}
		return null;
	}

	@Override
	public List<FileSaver> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Inject
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.insertFileSaver = new InsertFileSaver(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
