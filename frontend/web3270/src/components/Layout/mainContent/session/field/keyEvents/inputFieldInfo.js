export const currentFieldStart = (event, position) => {
    if (isProtected(event.target)) {
        return event.target;
    }

    for (let i = position; i >= 0; i--) {
        if (isProtected(inputField(event, i))) {
            return inputField(event, i + 1);
        }
    }
    
    return event.target;
}

export const currentFieldEnd = (event, position) => {
    if (isProtected(event.target)) {
        return event.target;
    }

    for (let i = position + 1; i < 1920; i++) {
        if (isProtected(inputField(event, i))) {
            return inputField(event, (i === 0 ? 0 : i - 1));
        }
    }
    
    return event.target;
}

export const getPositionNumber = (inputField) => {
    return inputField.id.replace("Position", "") * 1
}

export const getFieldId = (inputField) => {
    return inputField.id.replace("Field", "").replace("Position", "");
}

export const inputField = (event, position) => {
    const rc = rcPosition(position);
    return event.target.parentNode.parentNode.children[rc.row].children[rc.col];
}

export const isProtected = (inputField) => {
    return (inputField.className.search(" Prot") >= 0 );
}

export const nextInputField = (event, position) => {
    let fieldEndPosition = getPositionNumber(currentFieldEnd(event, position));

    // loop from current position until end of the screen
    for (let i = fieldEndPosition + 1; i < 1920; i++) {
        if (!isProtected(inputField(event, i))) {
            return inputField(event, i);
        }
    }

    // look from the beginning until current position
    for (let i = 0; i < position + 1; i++) {
        if (!isProtected(inputField(event, i))) {
            return inputField(event, i);
        }
    }

    return inputField(event, 0);
}

export const nextPosition = (position, desloc) => {

    position += desloc;
    
    if (position < 0) {
        position += 1920;
    }

    if (position > 1919) {
        position -= 1920;
    }

    return position;    
}

export const previowsInputField = (event, position) => {
    let fieldStartPosition = getPositionNumber(currentFieldStart(event, position));

    // look from current position until the beginning
    for (let i = fieldStartPosition - 1; i >= 0; i--) {
        if (!isProtected(inputField(event, i))) {
            return currentFieldStart(event, i);
        }
    }

    // look from the end until current position
    for (let i = 1919; i >= position; i--) {
        if (!isProtected(inputField(event, i))) {
            return currentFieldStart(event, i);
        }
    }

    return inputField(event, 0);
}

export const rcPosition = (position) => {
    const row = parseInt(position / 80);
    const col = parseInt(position - (row * 80));
    return {
        row: row,
        col: col
    }
}