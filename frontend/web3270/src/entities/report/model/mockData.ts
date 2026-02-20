import { Line, VariablesMap, Program } from './types';

// ── Mock Data ────────────────────────────────────────────────────────────────

const RAW_LINES: Line[] = [
  { num: 1,  text: "       IDENTIFICATION DIVISION.",                        type: "division" },
  { num: 2,  text: "       PROGRAM-ID. PAYROLL-CALC.",                       type: "normal" },
  { num: 3,  text: "       AUTHOR. JOHN DOE.",                               type: "normal" },
  { num: 4,  text: " ",                                                      type: "blank" },
  { num: 5,  text: "       ENVIRONMENT DIVISION.",                           type: "division" },
  { num: 6,  text: "       CONFIGURATION SECTION.",                          type: "section" },
  { num: 7,  text: " ",                                                      type: "blank" },
  { num: 8,  text: "       DATA DIVISION.",                                  type: "division" },
  { num: 9,  text: "       WORKING-STORAGE SECTION.",                        type: "section" },
  { num: 10, text: "       01  WS-EMPLOYEE-RECORD.",                         type: "normal" },
  { num: 11, text: "           05  WS-EMP-ID        PIC 9(6).",              type: "normal" },
  { num: 12, text: "           05  WS-EMP-NAME      PIC X(30).",             type: "normal" },
  { num: 13, text: "           05  WS-EMP-DEPT      PIC X(10).",             type: "normal" },
  { num: 14, text: "           05  WS-SALARY        PIC 9(7)V99.",           type: "normal" },
  { num: 15, text: "           05  WS-TAX-RATE      PIC V999.",              type: "normal" },
  { num: 16, text: "           05  WS-NET-PAY       PIC 9(7)V99.",           type: "normal" },
  { num: 17, text: "           05  WS-STATUS-FLAG   PIC X.",                 type: "normal" },
  { num: 18, text: "       01  WS-WORK-FIELDS.",                             type: "normal" },
  { num: 19, text: "           05  WS-CALC-TEMP     PIC 9(9)V99.",           type: "normal" },
  { num: 20, text: "           05  WS-ERROR-CODE    PIC X(4).",              type: "normal" },
  { num: 21, text: "           05  WS-PROCESS-DATE  PIC 9(8).",              type: "normal" },
  { num: 22, text: "           05  WS-LONG-DESC     PIC X(200).",            type: "normal" },
  { num: 23, text: " ",                                                      type: "blank" },
  { num: 24, text: "       PROCEDURE DIVISION.",                             type: "division" },
  { num: 25, text: "       0000-MAIN.",                                      type: "paragraph" },
  { num: 26, text: "           PERFORM 1000-INIT",                           type: "normal" },
  { num: 27, text: "           PERFORM 2000-PROCESS",                        type: "normal" },
  { num: 28, text: "           PERFORM 3000-FINALIZE",                       type: "normal" },
  { num: 29, text: "           STOP RUN.",                                   type: "normal" },
  { num: 30, text: " ",                                                      type: "blank" },
  { num: 31, text: "       1000-INIT.",                                      type: "paragraph" },
  { num: 32, text: "           MOVE SPACES TO WS-EMP-NAME",                  type: "normal" },
  { num: 33, text: "           MOVE ZEROS  TO WS-SALARY",                    type: "normal" },
  { num: 34, text: "           MOVE 'A'    TO WS-STATUS-FLAG",               type: "normal" },
  { num: 35, text: "           MOVE 20240315 TO WS-PROCESS-DATE",            type: "normal" },
  { num: 36, text: " ",                                                      type: "blank" },
  { num: 37, text: "       2000-PROCESS.",                                   type: "paragraph" },
  { num: 38, text: "           READ EMPLOYEE-FILE INTO WS-EMPLOYEE-RECORD",  type: "normal" },
  { num: 39, text: "           MOVE WS-SALARY TO WS-CALC-TEMP",              type: "normal" },
  { num: 40, text: "           MULTIPLY WS-TAX-RATE BY WS-CALC-TEMP",       type: "abend" },
  { num: 41, text: "           SUBTRACT WS-CALC-TEMP FROM WS-SALARY",       type: "normal" },
  { num: 42, text: "               GIVING WS-NET-PAY",                       type: "normal" },
  { num: 43, text: " ",                                                      type: "blank" },
  { num: 44, text: "       3000-FINALIZE.",                                  type: "paragraph" },
  { num: 45, text: "           WRITE OUTPUT-RECORD FROM WS-EMPLOYEE-RECORD", type: "normal" },
  { num: 46, text: "           MOVE 'P' TO WS-STATUS-FLAG",                  type: "normal" },
  { num: 47, text: "           CLOSE EMPLOYEE-FILE OUTPUT-FILE.",            type: "normal" },
];

export const MOCK_VARIABLES: VariablesMap = {
  "WS-EMP-ID":       { value: "047823",                              pic: "9(6)",    offset: "0x0000" },
  "WS-EMP-NAME":     { value: "MARIA SILVA FERREIRA SANTOS   ",      pic: "X(30)",   offset: "0x0006" },
  "WS-EMP-DEPT":     { value: "FINANCE   ",                          pic: "X(10)",   offset: "0x0024" },
  "WS-SALARY":       { value: "005750099",                           pic: "9(7)V99", offset: "0x002E" },
  "WS-TAX-RATE":     { value: "   ",                                 pic: "V999",    offset: "0x0037", corrupt: true },
  "WS-NET-PAY":      { value: "000000000",                           pic: "9(7)V99", offset: "0x003A" },
  "WS-STATUS-FLAG":  { value: "A",                                   pic: "X",       offset: "0x0043" },
  "WS-CALC-TEMP":    { value: "005750099  ",                         pic: "9(9)V99", offset: "0x0044" },
  "WS-ERROR-CODE":   { value: "0000",                                pic: "X(4)",    offset: "0x004F" },
  "WS-PROCESS-DATE": { value: "20240315",                            pic: "9(8)",    offset: "0x0053" },
  "WS-LONG-DESC":    { value: "Employee payroll processing record for fiscal year 2024. Department FINANCE. Grade: Senior. Contract type: CLT. Benefit tier: GOLD. Annual review status: PENDING. Manager: ROBERTO NASCIMENTO. Cost center: CC-4471-B. Project allocation: 60% PROJ-ALPHA, 40% PROJ-BETA.", pic: "X(200)", offset: "0x005B" },
};

// ── Annotate lines with all variable names mentioned in each line's text ─────
// Each line gets varNames: string[] — all known vars whose name appears in the text.
// This covers BOTH declarations and procedure references (MOVE, MULTIPLY, etc.)
const VAR_NAMES = Object.keys(MOCK_VARIABLES);

const ANNOTATED_LINES = RAW_LINES.map(line => ({
  ...line,
  varNames: VAR_NAMES.filter(v => {
    // Word-boundary match: variable name must appear as a standalone token
    const re = new RegExp(`(?<![A-Z0-9-])${v}(?![A-Z0-9-])`, "i");
    return re.test(line.text);
  }),
}));

export const MOCK_PROGRAM: Program = {
  name: "PAYROLL-CALC",
  abendCode: "S0C7",
  abendOffset: "000A4C",
  timestamp: "2024-03-15 14:32:07",
  lines: ANNOTATED_LINES,
};
