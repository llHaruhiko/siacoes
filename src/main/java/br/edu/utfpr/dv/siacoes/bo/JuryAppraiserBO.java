package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.JuryAppraiserDAO;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

public class JuryAppraiserBO {
	
	public JuryAppraiser findById(int id) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public JuryAppraiser findByAppraiser(int idJury, int idUser) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.findByAppraiser(idJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryAppraiser> listAppraisers(int idJury) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.listAppraisers(idJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, JuryAppraiser appraiser) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			SigetConfig config = new SigetConfigBO().findByDepartment(new JuryBO().findIdDepartment(appraiser.getJury().getIdJury()));
			
			if((config.getMaxFileSize() > 0) && (appraiser.getFile() != null) && ((appraiser.getIdJuryAppraiser() == 0) || !Arrays.equals(appraiser.getFile(), dao.getFile(appraiser.getIdJuryAppraiser()))) && (appraiser.getFile().length > config.getMaxFileSize())) {
				throw new Exception("O arquivo deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
			}
			if((config.getMaxFileSize() > 0) && (appraiser.getAdditionalFile() != null) && ((appraiser.getIdJuryAppraiser() == 0) || !Arrays.equals(appraiser.getAdditionalFile(), dao.getAdditionalFile(appraiser.getIdJuryAppraiser()))) && (appraiser.getAdditionalFile().length > config.getMaxFileSize())) {
				throw new Exception("O arquivo complementar deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
			}
			
			return dao.save(idUser, appraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean appraiserHasJury(int idJury, int idUser, Date date) throws Exception{
		try{
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.appraiserHasJury(idJury, idUser, date);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdDepartment(int idJuryAppraiser) throws Exception {
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.findIdDepartment(idJuryAppraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
