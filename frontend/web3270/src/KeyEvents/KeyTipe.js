import { isProtected } from "./InputFieldInfo";

export const isFunctionKey = (event) => {
    const keyInputs = ["F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", 
                       "F11", "F12", "PageUp", "PageDown", "Enter", "Pause"];
    return (keyInputs.indexOf(event.key) >= 0);
}

export const isSpecialKey = (event) => {
    const specialKeys = [ "Unidentified"
						, "Home"
						, "Control"
						, "Alt"
						, "Shift"
						, "Escape"
						, "Tab"
						, "CapsLock"
						, "Delete"
                        , "Insert"
                        , "Backspace"
						, "End" ];

    return (specialKeys.indexOf(event.key) >= 0);
}

export const isArrowKey = (event) => {
    const arrowKeys = [ "ArrowLeft"
						, "ArrowRight"
						, "ArrowUp"
						, "ArrowDown"];

    return (arrowKeys.indexOf(event.key) >= 0); 
}

export const isTypedChar = (event) => {
    const validChars = " âäàáãåçñ¢.<(+|&éêëèíîïìß!$*);¬-/ÂÄÀÁÃ"
    + "ÅÇÑ¦,%_>?øÉÊËÈÍÎÏÌ`:#@'=\"Øabcdefghi«»ð"
    + "ýþ±°jklmnopqrªºæ¸Æ¤µ~stuvwxyz¡¿ÐÝÞ®^£¥"
    + "·©§¶¼½¾[]¯¨´×{ABCDEFGHI­ôöòóõ}JKLMNOPQR"
    + "¹ûüùúÿ\\÷STUVWXYZ²ÔÖÒÓÕ0123456789³ÛÜÙÚ";
       
    return (  validChars.indexOf(event.key) >= 0
           && !isProtected(event.target)       );
}