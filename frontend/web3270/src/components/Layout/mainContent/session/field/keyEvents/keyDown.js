import handleFunctionKey    from "./HandleFunctionKey";
import { isFunctionKey }    from "./keyType";

const KeyDown = (event, positions) => {
    if (event.target.id.search("Position") < 0 ){
        return;
    }

    if (isFunctionKey(event)) {
        return handleFunctionKey(event, positions)
    }

    return true;
}

export default KeyDown;