import { isProtected }      from "./inputFieldInfo";
import { inputField }       from "./inputFieldInfo";
import { rcPosition }       from "./inputFieldInfo";
import { getPositionNumber }      from "./inputFieldInfo";
import { currentFieldEnd }  from "./inputFieldInfo";

export const deleteFieldValue = (event, position) => {

    if (isProtected(event.target)) {
        return false;
    } 

    let lastFieldPos = getPositionNumber(currentFieldEnd(event, position));

    for (let i = position; i < lastFieldPos; i++) {
        inputField(event, i).value = inputField(event, i + 1).value;
        inputField(event, i + 1).value = "";
    }
    inputField(event, lastFieldPos).value = "";
}

export const focusOn = (event, positionNumber) => {
    let rc = rcPosition(positionNumber);
    event.target.parentNode.parentNode.children[rc.row].children[rc.col].focus();
}