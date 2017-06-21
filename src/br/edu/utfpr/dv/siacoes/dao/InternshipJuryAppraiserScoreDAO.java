package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserScore;

public class InternshipJuryAppraiserScoreDAO {
	
	private Connection conn;
	
	public InternshipJuryAppraiserScoreDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public InternshipJuryAppraiserScoreDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}
	
	public boolean hasScore(int idInternshipJury, int idUser) throws SQLException{
		PreparedStatement stmt = this.conn.prepareStatement("SELECT internshipjuryappraiserscore.idInternshipJuryAppraiserScore FROM internshipjuryappraiserscore INNER JOIN internshipjuryappraiser ON internshipjuryappraiser.idInternshipJuryAppraiser=internshipjuryappraiserscore.idInternshipJuryAppraiser WHERE idInternshipJury=? AND idAppraiser=?");
		
		stmt.setInt(1, idInternshipJury);
		stmt.setInt(2, idUser);
		
		ResultSet rs = stmt.executeQuery();
		
		return rs.next();
	}
	
	public List<InternshipJuryAppraiserScore> listScores(int idInternshipJuryAppraiser) throws SQLException{
		PreparedStatement stmt = this.conn.prepareStatement("SELECT internshipjuryappraiserscore.*, internshipevaluationitem.description, internshipevaluationitem.ponderosity, internshipevaluationitem.type FROM internshipjuryappraiserscore INNER JOIN internshipevaluationitem ON internshipevaluationitem.idInternshipEvaluationItem=internshipjuryappraiserscore.idInternshipEvaluationItem WHERE idInternshipJuryAppraiser=? ORDER BY internshipevaluationitem.type, internshipevaluationitem.sequence");
		
		stmt.setInt(1, idInternshipJuryAppraiser);
		
		ResultSet rs = stmt.executeQuery();
		
		List<InternshipJuryAppraiserScore> list = new ArrayList<InternshipJuryAppraiserScore>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		if(list.size() == 0){
			stmt = this.conn.prepareStatement("SELECT idDepartment " +
					"FROM internshipjury INNER JOIN internshipjuryappraiser ON internshipjuryappraiser.idInternshipJury=internshipjury.idInternshipJury " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " +
					"WHERE idInternshipJuryAppraiser=?");
			
			stmt.setInt(1, idInternshipJuryAppraiser);
			
			rs = stmt.executeQuery();
			
			int idDepartment = 0;
			
			if(rs.next()){
				idDepartment = rs.getInt("idDepartment");
			}
			
			stmt = this.conn.prepareStatement("SELECT 0 as idInternshipJuryAppraiserScore, " + String.valueOf(idInternshipJuryAppraiser) + " as idInternshipJuryAppraiser, 0 as score, internshipevaluationitem.* FROM internshipevaluationitem WHERE active=1 AND idDepartment=? ORDER BY type, sequence");
			
			stmt.setInt(1, idDepartment);
			
			rs = stmt.executeQuery();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
		}
		
		return list;
	}
	
	public int save(InternshipJuryAppraiserScore score) throws SQLException{
		boolean insert = (score.getIdInternshipJuryAppraiserScore() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = this.conn.prepareStatement("INSERT INTO internshipjuryappraiserscore(idInternshipJuryAppraiser, idInternshipEvaluationItem, score) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = this.conn.prepareStatement("UPDATE internshipjuryappraiser SET idInternshipJuryAppraiser=?, idInternshipEvaluationItem=?, score=? WHERE idInternshipJuryAppraiserScore=?");
		}
		
		stmt.setInt(1, score.getInternshipJuryAppraiser().getIdInternshipJuryAppraiser());
		stmt.setInt(2, score.getInternshipEvaluationItem().getIdInternshipEvaluationItem());
		stmt.setDouble(3, score.getScore());
		
		if(!insert){
			stmt.setInt(4, score.getIdInternshipJuryAppraiserScore());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				score.setIdInternshipJuryAppraiserScore(rs.getInt(1));
			}
		}
		
		return score.getIdInternshipJuryAppraiserScore();
	}
	
	private InternshipJuryAppraiserScore loadObject(ResultSet rs) throws SQLException{
		InternshipJuryAppraiserScore score = new InternshipJuryAppraiserScore();
		
		score.setIdInternshipJuryAppraiserScore(rs.getInt("idInternshipJuryAppraiserScore"));
		score.getInternshipJuryAppraiser().setIdInternshipJuryAppraiser(rs.getInt("idInternshipJuryAppraiser"));
		score.getInternshipEvaluationItem().setIdInternshipEvaluationItem(rs.getInt("idInternshipEvaluationItem"));
		score.getInternshipEvaluationItem().setDescription(rs.getString("description"));
		score.getInternshipEvaluationItem().setPonderosity(rs.getDouble("ponderosity"));
		score.getInternshipEvaluationItem().setType(EvaluationItemType.valueOf(rs.getInt("type")));
		score.setScore(rs.getDouble("score"));
		
		return score;
	}

}