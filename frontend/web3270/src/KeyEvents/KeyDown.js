import handleFunctionKey    from "./HandleFunctionKey";
import handleSpecialKeys    from "./HandleSpecialKeys";
import handleArrowKeys      from "./HandleArrowKeys";
import handleType           from "./HandleType";
import { isFunctionKey }    from "./KeyTipe";
import { isSpecialKey }     from "./KeyTipe";
import { isArrowKey }       from "./KeyTipe";
import { isTypedChar }      from "./KeyTipe";
import { getPositionNumber }      from "./InputFieldInfo";

const KeyDown = (event, positions) => {
    if (event.target.id.search("Position") < 0 ){
        // if (isSpecialKey(event)) {
        //     return false;
        // }
        return;
    }

    let position = getPositionNumber(event.target);

    if (isFunctionKey(event)) {
        return handleFunctionKey(event, positions)
    }

    // if (isSpecialKey(event)) {
    //     handleSpecialKeys(event, position);
    //     return false;
    // }

    // if (isArrowKey(event)) {
    //     return handleArrowKeys(event, position, positions);
    // }

    // if (isTypedChar(event)) {
    //     return handleType(event, position);
    // }

    return true;
}

export default KeyDown;