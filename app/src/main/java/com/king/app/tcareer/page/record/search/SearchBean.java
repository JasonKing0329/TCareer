package com.king.app.tcareer.page.record.search;

public class SearchBean {

	private boolean courtOn;
	private boolean levelOn;
	private boolean matchOn;
	private boolean roundOn;
	private boolean regionOn;
	private boolean matchCountryOn;
	private boolean competitorOn;
	private boolean cptCountryOn;
	private boolean rankOn;
	private boolean dateOn;
	private boolean scoreOn;
	private boolean isWinnerOn;
	private String competitor;
	private String cptCountry;
	private String matchCountry;
	private String court;
	private String level;
	private String round;
	private String region;
	private String match;
	private boolean isWinner;
	private int rankMin, rankMax;
	private long date_start, date_end;
	private int scoreUser, scoreCpt;
	private boolean isScoreEachOther;

	public boolean isCourtOn() {
		return courtOn;
	}

	public void setCourtOn(boolean courtOn) {
		this.courtOn = courtOn;
	}

	public boolean isLevelOn() {
		return levelOn;
	}

	public void setLevelOn(boolean levelOn) {
		this.levelOn = levelOn;
	}

	public boolean isMatchOn() {
		return matchOn;
	}

	public void setMatchOn(boolean matchOn) {
		this.matchOn = matchOn;
	}

	public boolean isRoundOn() {
		return roundOn;
	}

	public void setRoundOn(boolean roundOn) {
		this.roundOn = roundOn;
	}

	public boolean isRegionOn() {
		return regionOn;
	}

	public void setRegionOn(boolean regionOn) {
		this.regionOn = regionOn;
	}

	public boolean isMatchCountryOn() {
		return matchCountryOn;
	}

	public void setMatchCountryOn(boolean matchCountryOn) {
		this.matchCountryOn = matchCountryOn;
	}

	public boolean isCompetitorOn() {
		return competitorOn;
	}

	public void setCompetitorOn(boolean competitorOn) {
		this.competitorOn = competitorOn;
	}

	public boolean isCptCountryOn() {
		return cptCountryOn;
	}

	public void setCptCountryOn(boolean cptCountryOn) {
		this.cptCountryOn = cptCountryOn;
	}

	public boolean isRankOn() {
		return rankOn;
	}

	public void setRankOn(boolean rankOn) {
		this.rankOn = rankOn;
	}

	public boolean isDateOn() {
		return dateOn;
	}

	public void setDateOn(boolean dateOn) {
		this.dateOn = dateOn;
	}

	public boolean isScoreOn() {
		return scoreOn;
	}

	public void setScoreOn(boolean scoreOn) {
		this.scoreOn = scoreOn;
	}

	public boolean isWinnerOn() {
		return isWinnerOn;
	}

	public void setWinnerOn(boolean winnerOn) {
		isWinnerOn = winnerOn;
	}

	public String getCompetitor() {
		return competitor;
	}

	public void setCompetitor(String competitor) {
		this.competitor = competitor;
	}

	public String getCptCountry() {
		return cptCountry;
	}

	public void setCptCountry(String cptCountry) {
		this.cptCountry = cptCountry;
	}

	public String getMatchCountry() {
		return matchCountry;
	}

	public void setMatchCountry(String matchCountry) {
		this.matchCountry = matchCountry;
	}

	public String getCourt() {
		return court;
	}

	public void setCourt(String court) {
		this.court = court;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public void setWinner(boolean winner) {
		isWinner = winner;
	}

	public int getRankMin() {
		return rankMin;
	}

	public void setRankMin(int rankMin) {
		this.rankMin = rankMin;
	}

	public int getRankMax() {
		return rankMax;
	}

	public void setRankMax(int rankMax) {
		this.rankMax = rankMax;
	}

	public long getDate_start() {
		return date_start;
	}

	public void setDate_start(long date_start) {
		this.date_start = date_start;
	}

	public long getDate_end() {
		return date_end;
	}

	public void setDate_end(long date_end) {
		this.date_end = date_end;
	}

	public int getScoreUser() {
		return scoreUser;
	}

	public void setScoreUser(int scoreUser) {
		this.scoreUser = scoreUser;
	}

	public int getScoreCpt() {
		return scoreCpt;
	}

	public void setScoreCpt(int scoreCpt) {
		this.scoreCpt = scoreCpt;
	}

	public boolean isScoreEachOther() {
		return isScoreEachOther;
	}

	public void setScoreEachOther(boolean scoreEachOther) {
		isScoreEachOther = scoreEachOther;
	}
}
