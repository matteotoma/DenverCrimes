package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> getAllCategories (){
		String sql = "SELECT DISTINCT offense_category_id AS c "
					+"FROM events " ;
		List<String> list = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(res.getString("c"));
			}
			
			conn.close();
			return list;
		} catch(SQLException e) {
			throw new RuntimeException("Errore nel db");
		}
	}
	
	public List<Month> getAllMonths(){
		String sql = "SELECT DISTINCT Month(reported_date) AS m "
					+"FROM events ";
		List<Month> list = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				int mese = res.getInt("m");
				list.add(Month.of(mese));
			}
			
			conn.close();
			return list;
		} catch(SQLException e) {
			throw new RuntimeException("Errore nel db");
		}
			
	}

	public List<Adiacenza> getAdiacenze(String categoria, int value) {
		String sql= "SELECT e1.offense_type_id AS o2, e2.offense_type_id o1, COUNT(DISTINCT(e1.neighborhood_id)) peso "
					+"FROM EVENTS AS e1, EVENTS AS e2 "
					+"WHERE e1.offense_category_id= ? "
					+"AND e2.offense_category_id=? " 
					+"AND MONTH(e1.reported_date)=? " 
					+"AND MONTH(e2.reported_date)=? "
					+"AND e1.offense_type_id != e2.offense_type_id "
					+"AND e1.neighborhood_id=e2.neighborhood_id "
					+"GROUP BY e1.offense_type_id, e2.offense_type_id";
		List<Adiacenza> adiacenze = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setString(1, categoria);
			st.setString(2, categoria);
			st.setInt(3, value);
			st.setInt(4, value);
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				adiacenze.add(new Adiacenza(res.getString("o1"), res.getString("o2"), res.getDouble("peso")));
			}
			
			conn.close();
			return adiacenze;
		} catch(SQLException e) {
			throw new RuntimeException("Errore nel db");
		}
	}

}
