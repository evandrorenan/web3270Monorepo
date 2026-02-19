import { Field } from '../model/slice';

// Helper to generate fields based on layout
const createField = (
  id: number | string,
  start: number, // 1-based index
  length: number,
  text: string,
  isProtected: boolean,
  isHidden: boolean,
  isHighIntensity: boolean,
  color?: string
): any => {
    // Backend field DTO structure (as expected by adaptScreenData in middleware)
    return {
        fieldId: id,
        start,
        end: start + length - 1,
        text,
        isProtected,
        isHidden,
        isHighIntensity,
        color
    };
};

// Layout: 24 rows x 80 columns = 1920 chars
const ROW_1 = 1;
const ROW_3 = 1 + (2 * 80);
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

export const mockLoginScreen = {
    content: BACKGROUND_TEXT,
    cursorPos: ROW_5 + 16, // Cursor at start of User input
    fields: [
        // Title (Protected) - High Intensity White - On Row 3 to match background
        createField('title', ROW_3 + 27, 26, "Web3270 Mock System", true, false, true, "White"),

        // User Label (Protected) - Green
        createField('lbl_user', ROW_5 + 10, 5, "User:", true, false, false, "Green"),

        // User Input (Unprotected) - Turquoise/Cyan
        createField('inp_user', ROW_5 + 16, 10, "", false, false, false, "Turquoise"),

        // Password Label (Protected) - Green
        createField('lbl_pass', ROW_7 + 10, 9, "Password:", true, false, false, "Green"),

        // Password Input (Unprotected, Hidden) - Red (usually hidden doesn't matter color but for demo)
        createField('inp_pass', ROW_7 + 20, 10, "", false, true, false, "Red"),

        // Footer (Protected) - Blue
        createField('footer', ROW_24, 25, "PF3=Exit  Enter=Submit", true, false, true, "Blue"),
    ]
};

export const mockLoggedInScreen = {
    content: "                                                                                ".repeat(10) +
             "                    WELCOME TO THE SYSTEM!                                      " +
             "                                                                                ".repeat(13),
    cursorPos: 1,
    fields: [
        createField('welcome', ROW_1 + (10 * 80) + 20, 22, "WELCOME TO THE SYSTEM!", true, false, true, "Yellow")
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
