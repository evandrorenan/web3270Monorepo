package br.com.evandrorenan.web3270scripts.bean;

@Data
public class Node {
	private static final int COLS_PER_PAGE = 80;
	
	private static boolean firstPage = true;
	
	private ACTION nodeType;
	private int startRow = 0;
	private int startCol = 0;
	private int endRow = 0;
	private int endCol = 0;
	private String pfLeft;
	private String pfRight;
	private long waitTime = 0;
	private boolean discardTsoNotification = false;
	private String text = "";
	private Integer indexNextNode = null;
	private Integer indexForkNode = null;
	private ForkCondition forkCondition = null;
	private List<RangeCopy> listRangeCopy = null;
	
	public List<String> execute(IMySession session) throws ExceptionResponse, InterruptedException {
		System.out.println(this.toString());
		switch (nodeType) {
		case CHECK_SKIP_NEXT:
			return this.executeCheckSkipNext(session);

		case CHECK:
			this.executeCheck(session);
			break;

		case SEND_KEYS:
			this.executeSendKeys(session);
			break;

		case WAIT_SCREEN_CHANGE:
			this.executeWaitScreenChange(session);
			break;

		case WAIT_TIME:
			this.executeWaitTime();
			break;

		case COPY:
			return this.executeCopy(session);

		case LOOP_TIMES:
			this.executeTimes();
			break;

		case LOOP_UNTIL:
			this.executeLoopUntil();
			break;

		default:
			break;
		}
		return Collections.emptyList();
	}
	
	public Integer getIndexNextNode(List<String> page) throws ExceptionResponse {
		if (this.forkCondition == null 
		|| !this.forkCondition.check(page, this.startCol, this.forkCondition)){
			return this.indexNextNode;
		} else {
			return this.indexForkNode;
		}
	}
	
	private void executeLoopUntil() {
		// TODO I stopped here
		
	}

	private void executeTimes() {
		if (this.forkCondition.getMaxReps() > 0) {
			this.forkCondition.setMaxReps(this.forkCondition.getMaxReps() - 1);
		}
	}

	private List<String> executeCopy(IMySession session) throws ExceptionResponse, InterruptedException {
		this.checkFirstPage(session);
		
		List<String> copiedList = new ArrayList<>();
		
		int pageCount = 0;
		for (RangeCopy page : this.listRangeCopy) {
			if (pageCount != 0) {
				session.sendKeys("[" + pfRight + "]");				
			}
			copiedList = this.joinLists(
					copiedList, 
					session.getString(page.getStartRow(), page.getStartCol(), page.getEndRow(), page.getEndCol()));
			pageCount++;
		}
		
		this.pageLeft(session, pageCount);
		
		return copiedList;
	}
	
	private void pageLeft(IMySession session, int pageCount) throws ExceptionResponse, InterruptedException {
		
		for (int i = 0; i < pageCount - 1; i++) { 
			session.sendKeys("[" + pfLeft + "]");	
		}				
	}

	private List<String> joinLists(List<String> listString1, List<String> listString2) {

		if (listString1 == null && listString2 == null) {
			return Collections.emptyList();
		}
		
		if (listString1 == null ) {
			return listString2;
		}
		
		if (listString2 == null ) {
			return listString1;
		}
		
		List<String> newList = new ArrayList<>();
		for (int i = 0; i < Math.max(listString1.size(), listString2.size()); i++) {
			newList.add(
					(i >= listString1.size() ? "" : listString1.get(i)) +
					(i >= listString2.size() ? "" : listString2.get(i)));
		}
		
		return newList;
	}

	private void checkFirstPage(IMySession session) throws ExceptionResponse, InterruptedException {
		if (!firstPage) {
			session.sendKeys(MySessionConstants.F8_STR);
		}
		firstPage = false;
	}
	
	private List<String> executeCopyOld(IMySession session) throws ExceptionResponse, InterruptedException {
		List<String> copyList = new ArrayList<>();
		if (!firstPage) {
			session.sendKeys(MySessionConstants.F8_STR);
		}
		firstPage = false;
		
		//execute this 'n' times, according with the number of right paginations
		for (int page = 1; page <= ( this.endCol / COLS_PER_PAGE) + 1; page++ ) {	//'page' is for left and right paginations
			//execute this 'n' times, according with the numer of lines per page
			for (int row = this.startRow; row <= this.endRow; row++) {				//execute this 'n' times, according with the numer of lines per page
				if (this.startCol <= page * COLS_PER_PAGE ) {					
					copyList.add(session.getString(row, page == 1? startCol : 1, this.getCalcEndColPerPage(page)));
				}
			}			
			if (page != (int) ( this.endCol / COLS_PER_PAGE) + 1) {
				session.sendKeys("[" + pfRight + "]");								//send pagination key when not in the last page
			}
		}
		
		for (int i = 0; i < ((int) (this.endCol / 80)); i++) {						//returns to page 1
			session.sendKeys("[" + pfLeft + "]");
		}
		
		List <String> copiedLines = new ArrayList<>();								
		int linhas = this.endRow - this.startRow + 1;
		for (int i = 0; i <= this.endRow - this.startRow; i++) {					//concatenate same line strings from different pages
			StringBuilder stringBuilder = new StringBuilder();
			for (int j = 0; j < copyList.size() / linhas; j++) {					
				stringBuilder.append(copyList.get(i + (linhas * j)));
			}
			copiedLines.add(stringBuilder.toString());
		}
		copyList.clear();
		return copiedLines;
	}
	
	private int getCalcEndColPerPage(int page) {
		
		if (page == 1) {
			if (this.endCol <= COLS_PER_PAGE) {
				return this.endCol;
			}
		} else {
			if ( page == (int) (this.endCol / COLS_PER_PAGE) + 1) { //last page
				return this.endCol - ((page - 1) * COLS_PER_PAGE);
			}
		}
		return COLS_PER_PAGE;
	}

	private void executeWaitTime() throws InterruptedException {		
		Thread.sleep(this.waitTime);	
	}

	private void executeWaitScreenChange(IMySession session) throws ExceptionResponse {
		String telaNova = session.getString();
		String telaAnt = telaNova;
		try {
			Thread.sleep(100L);
			System.out.print("Wait screen change 10 miliseconds - node");
			for (long repeticoes = 100L; telaNova.equals(telaAnt) && repeticoes > 0L; repeticoes -= 10L) {
				System.out.print(".");
				Thread.sleep(100L);
				telaNova = session.getString();
			}
			System.out.println("");
		} catch (Exception e) {
			throw new ExceptionResponse(e.getMessage(), e.getLocalizedMessage());
		}
	}

	private void executeSendKeys(IMySession session) throws InterruptedException, ExceptionResponse {
		session.sendKeys(this.text, this.startRow, this.startCol);
	}

	private List<String> executeCheckSkipNext(IMySession session) throws ExceptionResponse {
		List<String> returnList = new ArrayList<>();
		
		String textFromScreen = session.getString(this.startRow, this.startCol, this.text.length());
		if (! textFromScreen.equals(this.text)) {
			returnList.add(AbendToolsSuiteConstants.SKIP_NEXT);
		}
		return returnList;
	}
	
	private void executeCheck(IMySession session) throws ExceptionResponse {
		String textFromScreen = session.getString(this.startRow, this.startCol, this.text.length());
		if (! textFromScreen.equals(this.text)) {
			throw new ExceptionResponse(
				"Unexpected Screen", 
				"Expected: '" + this.text + "' but found: '" + textFromScreen + "'");
		}
	}
	
	public Node(String strAction) throws Exception{
		
		strAction = strAction.replaceAll("\t", " ");
		strAction = strAction.trim();
		String[] tokens = strAction.split("\\s+", 4);

		this.buildCheckNode(tokens);
		this.buildCheckSkipNextNode(tokens);
		this.buildSendKeysNode(tokens);
		this.buildWaitScreenChangeNode(tokens);
		this.buildWaitTimeNode(tokens);
		this.buildCopyNode(strAction);
		this.buildLoopTimesNode(tokens);
		this.buildLoopUntilNode(strAction);			
		this.buildEndLoopNode(tokens);
		
		if (this.nodeType == null) {
			throw new Exception("Invalid command: " + strAction);
		}
	}

	private void buildEndLoopNode(String[] tokens) {
		if (!tokens[0].equals(ACTION.END_LOOP.getActionName())) {
			return;
		}
		this.nodeType = ACTION.END_LOOP;
	}

	private void buildLoopUntilNode(String strAction) {
		String[] tokens = strAction.split("\\s+", 3);
		
		if (!tokens[0].equals(ACTION.LOOP_UNTIL.getActionName())) {
			return;
		}

		this.nodeType = ACTION.LOOP_UNTIL;
		this.forkCondition = new ForkCondition(
				Integer.parseInt(tokens[1]),
				tokens[2].replaceAll("\"", ""));
		return;
	}

	private void buildLoopTimesNode(String[] tokens) {
		if (!tokens[0].equals(ACTION.LOOP_TIMES.getActionName())) {
			return;
		}
		this.nodeType = ACTION.LOOP_TIMES;
		this.forkCondition = new ForkCondition(
				Integer.parseInt(tokens[1]));
	}

	private void buildCopyNode(String strAction) {
		String[] tokens = strAction.split("\\s+");
		
		if (!tokens[0].equals(ACTION.COPY.getActionName())) {
			return;
		}
		
		this.listRangeCopy = new ArrayList<>();
		RangeCopy rangeCopy = new RangeCopy();
		
		this.nodeType = ACTION.COPY;
		this.startRow = Integer.parseInt(tokens[1]);
		this.startCol = Integer.parseInt(tokens[2]);
		this.endRow = Integer.parseInt(tokens[3]);
		this.endCol = Integer.parseInt(tokens[4]);
		
		rangeCopy.setStartRow(Integer.parseInt(tokens[1]));
		rangeCopy.setStartCol(Integer.parseInt(tokens[2]));
		rangeCopy.setEndRow  (Integer.parseInt(tokens[3]));
		rangeCopy.setEndCol  (Integer.parseInt(tokens[4]));
		
		this.listRangeCopy.add(rangeCopy);
		
		if (tokens.length > 5) {
			this.pfLeft = tokens[5];
			this.pfRight = tokens[6];
		}
		
		for (int i = 7; i + 1 < tokens.length; i = i + 2) {
			rangeCopy = new RangeCopy();
			rangeCopy.setStartRow(Integer.parseInt(tokens[1]));
			rangeCopy.setEndRow  (Integer.parseInt(tokens[3]));
			
			rangeCopy.setStartCol(Integer.parseInt(tokens[i]));
			rangeCopy.setEndCol  (Integer.parseInt(tokens[i + 1]));
			this.listRangeCopy.add(rangeCopy);
		}
		
		return;
	}

	private void buildCopyNodeOld(String strAction) {
		String[] tokens = strAction.split("\\s+", 7);
		
		if (!tokens[0].equals(ACTION.COPY.getActionName())) {
			return;
		}
		
		this.nodeType = ACTION.COPY;
		this.startRow = Integer.parseInt(tokens[1]);
		this.startCol = Integer.parseInt(tokens[2]);
		this.endRow = Integer.parseInt(tokens[3]);
		this.endCol = Integer.parseInt(tokens[4]);
		if (tokens.length > 5) {
			this.pfLeft = tokens[5];
			this.pfRight = tokens[6];
		}
	}

	private void buildWaitTimeNode(String[] tokens) {
		if (!tokens[0].equals(ACTION.WAIT_TIME.getActionName())) {
			return;
		}
		this.nodeType = ACTION.WAIT_TIME;
		this.waitTime = Integer.parseInt(tokens[1]);
	}

	private void buildWaitScreenChangeNode(String[] tokens) {
		if (!tokens[0].equals(ACTION.WAIT_SCREEN_CHANGE.getActionName())) {
			return;
		}

		this.nodeType = ACTION.WAIT_SCREEN_CHANGE;
		if (tokens.length > 1) {
			this.discardTsoNotification = true;
		}
	}

	private void buildSendKeysNode(String[] tokens) {
		if (!tokens[0].equals(ACTION.SEND_KEYS.getActionName())) {
			return;
		}

		this.nodeType = ACTION.SEND_KEYS;
		this.startRow = Integer.parseInt(tokens[1]);
		this.startCol = Integer.parseInt(tokens[2]);
		this.text = tokens[3].replaceAll("\"", "");
	}

	private void buildCheckSkipNextNode(String[] tokens) {
		if (!tokens[0].equals(ACTION.CHECK_SKIP_NEXT.getActionName())) {
			return;
		}
		
		this.nodeType = ACTION.CHECK_SKIP_NEXT;
		this.startRow = Integer.parseInt(tokens[1]);
		this.startCol = Integer.parseInt(tokens[2]);
		this.text = tokens[3].replaceAll("\"", "");
		//			TODO validarNodeCheck()
	}

	private void buildCheckNode(String[] tokens) {
		if (!tokens[0].equals(ACTION.CHECK.getActionName())) {
			return;
		}
		
		this.nodeType = ACTION.CHECK;
		this.startRow = Integer.parseInt(tokens[1]);
		this.startCol = Integer.parseInt(tokens[2]);
		this.text = tokens[3].replaceAll("\"", "");
		//			TODO validarNodeCheck()
	}

	public static boolean isFirstPage() {
		return firstPage;
	}

	public static void setFirstPage(boolean firstPage) {
		Node.firstPage = firstPage;
	}
}