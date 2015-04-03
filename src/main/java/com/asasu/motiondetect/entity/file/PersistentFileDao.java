package com.asasu.motiondetect.entity.file;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.asasu.motiondetect.interfaces.IPersistentFileDao;

public class PersistentFileDao implements IPersistentFileDao {
	private Log log = LogFactory.getLog(PersistentFileDao.class);
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private UpdatePersistentFileInformation updatePersistentFileInformation;
	private InsertPersistentFileInformation insertPersistentFileInformation;
	private SelectPersistentFileInformationByPath selectPersistentFileInformationByPath;

	private static final class PersistentFileInformationMapper implements
	RowMapper<PersistentFileInformation> {
		@Override
		public PersistentFileInformation mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			PersistentFileInformation pf = new PersistentFileInformation();
			pf.setId(rs.getLong("id"));
			pf.setFilePath(rs.getString("file_path"));
			pf.setMd5(rs.getString("md5"));
			pf.setLastModified(rs.getLong("last_modified"));
			pf.setFileSaverId(rs.getLong("file_saver_id"));
			pf.setRemoteId(rs.getString("remote_id"));
			return pf;
		}
	}

	private static final class UpdatePersistentFileInformation extends
	SqlUpdate {
		private static final String SQL_UPDATE_PF = "update file_information set file_path=:file_path, md5=:md5,last_modified=:last_modified, remote_id=:remote_id where file_saver_id=:file_saver_id and file_path=:file_path";

		public UpdatePersistentFileInformation(DataSource dataSource) {
			super(dataSource, SQL_UPDATE_PF);
			super.declareParameter(new SqlParameter("file_path", Types.VARCHAR));
			super.declareParameter(new SqlParameter("md5", Types.VARCHAR));
			super.declareParameter(new SqlParameter("last_modified",
					Types.BIGINT));
			super.declareParameter(new SqlParameter("file_saver_id",
					Types.BIGINT));
			super.declareParameter(new SqlParameter("remote_id", Types.VARCHAR));

		}
	}

	private static final class InsertPersistentFileInformation extends
	SqlUpdate {
		private static final String SQL_INSERT_PF = "insert into file_information (file_path, md5, last_modified, file_saver_id, remote_id) values (:file_path,:md5, :last_modified, :file_saver_id, :remote_id)";

		public InsertPersistentFileInformation(DataSource dataSource) {
			super(dataSource, SQL_INSERT_PF);
			super.declareParameter(new SqlParameter("file_path", Types.VARCHAR));
			super.declareParameter(new SqlParameter("md5", Types.VARCHAR));
			super.declareParameter(new SqlParameter("last_modified",
					Types.BIGINT));
			super.declareParameter(new SqlParameter("file_saver_id",
					Types.BIGINT));
			super.declareParameter(new SqlParameter("remote_id", Types.VARCHAR));
			super.setGeneratedKeysColumnNames(new String[] { "id" });
			super.setReturnGeneratedKeys(true);
		}
	}

	private static final class SelectPersistentFileInformationByPath extends
	MappingSqlQuery<PersistentFileInformation> {
		private static final String SQL_FIND_BY_FILE_PATH = "select id, file_path, md5, last_modified, file_saver_id, remote_id from file_information where file_path =:file_path";

		public SelectPersistentFileInformationByPath(DataSource dataSource) {
			super(dataSource, SQL_FIND_BY_FILE_PATH);
			super.declareParameter(new SqlParameter("file_path", Types.VARCHAR));
		}

		@Override
		protected PersistentFileInformation mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			PersistentFileInformation pfi = new PersistentFileInformation();
			pfi.setId(rs.getLong("id"));
			pfi.setFilePath(rs.getString("file_path"));
			pfi.setMd5(rs.getString("md5"));
			pfi.setLastModified((rs.getLong("last_modified")));
			pfi.setFileSaverId(rs.getLong("file_saver_id"));
			pfi.setRemoteId(rs.getString("remote_id"));
			return pfi;
		}
	}

	@Override
	public void delete(PersistentFileInformation pf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(PersistentFileInformation pf) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("file_path", pf.getFilePath());
		paramMap.put("md5", pf.getMd5());
		paramMap.put("last_modified", pf.getLastModified());
		paramMap.put("file_saver_id", pf.getFileSaverId());
		paramMap.put("remote_id", pf.getRemoteId());
		updatePersistentFileInformation.updateByNamedParam(paramMap);
		log.debug("File updated with path: " + pf.getFilePath());
	}

	@Override
	public PersistentFileInformation findByPath(String filePath) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("file_path", filePath);
		List<PersistentFileInformation> pfi = selectPersistentFileInformationByPath
				.executeByNamedParam(paramMap);
		if (pfi.size() > 0) {
			return pfi.get(0);
		}
		return null;

	}

	@Override
	public List<PersistentFileInformation> findAll() {
		String sql = "select id, file_path, md5, last_modified, file_saver_id, remote_id from file_information";
		return jdbcTemplate.query(sql, new PersistentFileInformationMapper());
	}

	@Override
	public List<PersistentFileInformation> findByPathAndRemoteId(
			String filePath, String remoteId) {
		String sql = "select id, file_path, md5, last_modified, file_saver_id, remote_id from file_information where file_path=:file_path and remote_id=:remote_id";
		return jdbcTemplate.query(sql,
				new RowMapper<PersistentFileInformation>() {

			@Override
			public PersistentFileInformation mapRow(ResultSet rs,
					int rowNum) throws SQLException {
				PersistentFileInformation pfi = new PersistentFileInformation();
				pfi.setId(rs.getLong("id"));
				pfi.setFilePath(rs.getString("file_path"));
				pfi.setMd5(rs.getString("md5"));
				pfi.setLastModified((rs.getLong("last_modified")));
				pfi.setFileSaverId(rs.getLong("file_saver_id"));
				pfi.setRemoteId(rs.getString("remote_id"));
				return pfi;
			}
		});
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
		this.updatePersistentFileInformation = new UpdatePersistentFileInformation(
				dataSource);
		this.insertPersistentFileInformation = new InsertPersistentFileInformation(
				dataSource);
		this.selectPersistentFileInformationByPath = new SelectPersistentFileInformationByPath(
				dataSource);
	}

	@Override
	public PersistentFileInformation insert(PersistentFileInformation pf) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("file_path", pf.getFilePath());
		paramMap.put("md5", pf.getMd5());
		paramMap.put("last_modified", pf.getLastModified());
		paramMap.put("file_saver_id", pf.getFileSaverId());
		paramMap.put("remote_id", pf.getRemoteId());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		insertPersistentFileInformation.updateByNamedParam(paramMap, keyHolder);
		pf.setId(keyHolder.getKey().longValue());
		log.debug("New file inserted with id: " + pf.getId());
		return pf;
	}

	@Override
	public PersistentFileInformation findByPathAndFileSaverId(String filePath,
			Long fileSaverId) {
		String sql = "select id, file_path, file_saver_id, md5, last_modified, remote_id from file_information where file_path =:file_path and file_saver_id =:file_saver_id";
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("file_path", filePath);
		namedParameters.put("file_saver_id", fileSaverId);
		List<PersistentFileInformation> pfi = namedParameterJdbcTemplate.query(
				sql, namedParameters,
				new RowMapper<PersistentFileInformation>() {
					@Override
					public PersistentFileInformation mapRow(ResultSet rs,
							int rowNum) throws SQLException {
						PersistentFileInformation pfi = new PersistentFileInformation();
						pfi.setId(rs.getLong("id"));
						pfi.setFilePath(rs.getString("file_path"));
						pfi.setFileSaverId(rs.getLong("file_saver_id"));
						pfi.setLastModified(rs.getLong("last_modified"));
						pfi.setRemoteId(rs.getString("remote_id"));
						return pfi;
					}
				});
		if (pfi.isEmpty()) {
			return null;
		}
		return pfi.get(0);
	}
}
