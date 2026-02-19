import { Field } from '../model/slice';

// Helper to generate fields based on layout
const createField = (
  id: number | string,
  start: number, // 1-based index
  length: number,
  text: string,
  isProtected: boolean,
  isHidden: boolean,
  isHighIntensity: boolean
): any => {
    // Backend field DTO structure (as expected by adaptScreenData in middleware)
    return {
        fieldId: id,
        start,
        end: start + length - 1,
        text,
        isProtected,
        isHidden,
        isHighIntensity
    };
};

// Layout: 24 rows x 80 columns = 1920 chars
// Row 1 starts at 1. Row 2 starts at 81.
const ROW_1 = 1;
const ROW_5 = 1 + (4 * 80);
const ROW_7 = 1 + (6 * 80);
const ROW_24 = 1 + (23 * 80);

const BACKGROUND_TEXT =
  "                                                                                " + // 1
  "                                                                                " + // 2
  "                           Web3270 Mock System                                  " + // 3
  "                                                                                " + // 4
  "          User:                                                                 " + // 5
  "                                                                                " + // 6
  "          Password:                                                             " + // 7
  "                                                                                " + // 8
  "                                                                                " + // 9
  "                                                                                ".repeat(14) + // 10-23
  "PF3=Exit  Enter=Submit                                                          "; // 24

// Adjust background text to match fields?
// Usually background text is what's "underneath".
// But let's keep it simple. The content string will be the full screen text representation.
// Fields will overlay.
// Actually, for content, let's make it reflect the labels.

export const mockLoginScreen = {
    content: BACKGROUND_TEXT,
    cursorPos: ROW_5 + 16, // Cursor at start of User input
    fields: [
        // Title (Protected)
        createField('title', ROW_1 + 27, 26, "Web3270 Mock System", true, false, true),

        // User Label (Protected)
        createField('lbl_user', ROW_5 + 10, 5, "User:", true, false, false),

        // User Input (Unprotected)
        createField('inp_user', ROW_5 + 16, 10, "", false, false, false),

        // Password Label (Protected)
        createField('lbl_pass', ROW_7 + 10, 9, "Password:", true, false, false),

        // Password Input (Unprotected, Hidden)
        createField('inp_pass', ROW_7 + 20, 10, "", false, true, false),

        // Footer (Protected)
        createField('footer', ROW_24, 25, "PF3=Exit  Enter=Submit", true, false, true),
    ]
};

export const mockLoggedInScreen = {
    content: "                                                                                ".repeat(10) +
             "                    WELCOME TO THE SYSTEM!                                      " +
             "                                                                                ".repeat(13),
    cursorPos: 1,
    fields: [
        createField('welcome', ROW_1 + (10 * 80) + 20, 22, "WELCOME TO THE SYSTEM!", true, false, true)
    ]
};

let currentScreen = mockLoginScreen;

export const MockSessionService = {
    connect: async () => {
        await new Promise(resolve => setTimeout(resolve, 500)); // Simulate delay
        return { sessionId: 'mock-session-001' };
    },

    getScreen: async () => {
        await new Promise(resolve => setTimeout(resolve, 200));
        return currentScreen;
    },

    sendKeys: async (sessionId: string, keys: string) => {
        await new Promise(resolve => setTimeout(resolve, 300));
        console.log(`[Mock] Received keys for ${sessionId}: ${keys}`);

        if (keys === '[enter]') {
            // Simulate login success regardless of input
            currentScreen = mockLoggedInScreen;
        } else if (keys === '[pf3]') {
            // Reset to login
            currentScreen = mockLoginScreen;
        }

        return currentScreen;
    }
};
