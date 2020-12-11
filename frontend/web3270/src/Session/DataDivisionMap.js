import React, { Component } from 'react';
import { connect          } from 'react-redux';

import './DataDivisionMap.css';

class DataDivisionMap extends Component {

    constructor(props) {
        super();
        let dataItemsIndexes = new Array(props.report.dataDivisionMap.length);

        this.state = { 
            pinnedDataItems: [],
            unpinnedHexFlag: dataItemsIndexes,
            selectedItem: ""
        }
    }

    pinDataItem = (event) => {
        if (event.target.checked ) {
            const index = event.target.id.replace("checkbox", "") * 1;
            let localState = Object.assign({}, this.state);
            localState.pinnedDataItems.push(this.props.report.dataDivisionMap[index]);
            this.setState(localState);
            event.target.checked = false;
        }
    }
    
    unpinDataItem = (event) => {
        if (! event.target.checked ) {
            const index = event.target.id.replace("pinnedCheckBox", "") * 1;
            let localState = Object.assign({}, this.state);
            localState.pinnedDataItems.splice(index, 1);
            this.setState(localState);
        }
    }

    toggleHexPinnedDataItem = (event) => {
        const index = event.target.id.replace("checkboxTogglePinnedHexContent", "") * 1;
        let localState = Object.assign({}, this.state);
        if (event.target.checked) {
            localState.pinnedDataItems[index].showHex = true;
        } else {
            localState.pinnedDataItems[index].showHex = false;
        }
        this.setState(localState);
        return true;
    }
    
    toggleHexUnpinnedDataItem = (event) => {
        const index = event.target.id.replace("checkboxToggleUnpinnedHexContent", "") * 1;
        let localState = Object.assign({}, this.state);
        if (event.target.checked) {
            localState.unpinnedHexFlag[index] = true;
        } else {
            localState.unpinnedHexFlag[index] = false;
        }
        this.setState(localState);
        return true;
    }

    selectUnpinnedDataItem = (event) => {
        const index = event.target.id.replace("dataDivisionItemName", "") * 1;        
        let localState = Object.assign({}, this.state);
        localState.selectedItem = event.target.textContent + ":\n";

        if (event.target.nextElementSibling.id.includes("Hex")) {
            localState.selectedItem += typeof this.props.report.dataDivisionMap[index].hexContent === 'undefined' ? "" 
                                           : this.props.report.dataDivisionMap[index].hexContent;
        } else {
            localState.selectedItem += typeof this.props.report.dataDivisionMap[index].ebcdicContent === 'undefined' ? "" 
                                           : this.props.report.dataDivisionMap[index].ebcdicContent;
        }
        this.setState(localState);
    }
    
    selectPinnedDataItem = (event) => {
        const index = event.target.id.replace("dataDivisionPinnedName", "") * 1;        
        let localState = Object.assign({}, this.state);

        const selectedItemLineId = localState.pinnedDataItems[index].lineId;
        const selectedItem = this.props.report.dataDivisionMap.find(item => { return item.lineId === selectedItemLineId });

        localState.selectedItem = event.target.textContent + ":\n";
        if (event.target.nextElementSibling.id.includes("Hex")) {
            localState.selectedItem += typeof selectedItem.hexContent === 'undefined' ? "" 
                                            : selectedItem.hexContent;
        } else {
            localState.selectedItem += typeof selectedItem.ebcdicContent === 'undefined' ? "" 
                                            : selectedItem.ebcdicContent;
        }
        this.setState(localState);
    }
    
    render() {
        console.log("Data Division Map" + this.props.dataDivisionMap);        

        return (
            <div className="DataDivision" key="dataDivision">
                <div class="DataItemsList" key="dataDivisionItemsList">
                    <div className="DataDiv" key={"dataDivisionItemsTitle"}>
                        <input type="checkbox" class="checkboxTitle" key={"pinCheckboxTitle"}/>
                        <div className="DataDivItemName" key={"dataDivisionTitleName"}>
                            Data Item
                        </div>
                        <div className="DataDivContent" key={"dataDivisionTitleEbcdicContent"}>
                            Content
                        </div>
                        <div className="ToggleHex" key={"dataDivisionTitleToggleHex"}>
                            Hex
                        </div>
                    </div>

                    {this.state.pinnedDataItems.map((dataDivisionItem, index) => {
                        return  <div className="DataDiv" 
                            key={"dataDivisionPinnedItems" + index}>
                            <input  type="checkbox" 
                                    key={"checkboxPinned" + index} 
                                    checked />
                            <div className="DataDivItemName Pinned" 
                                    id={"dataDivisionPinnedName" + index} 
                                    key={"dataDivisionPinnedName" + index}
                                    onClick={this.selectPinnedDataItem} >
                                {dataDivisionItem.dataName}
                            </div>
                            {typeof dataDivisionItem.showHex === 'undefined' || dataDivisionItem.showHex === false ? 
                                <div className="DataDivContent" 
                                        id={"dataDivisionPinnedEbcdicContent" + index}
                                        key={"dataDivisionPinnedEbcdicContent" + index}>
                                    {typeof dataDivisionItem.ebcdicContent === 'undefined' ? "" :
                                    dataDivisionItem.ebcdicContent[0].substring(0, 
                                        dataDivisionItem.ebcdicContent[0].length > 50 ? 50 : dataDivisionItem.ebcdicContent[0].length) }
                                </div>
                            :
                                <div className="DataDivHex" 
                                        id={"dataDivisionPinnedHexContent" + index}
                                        key={"dataDivisionPinnedHexContent" + index}>
                                    {typeof dataDivisionItem.hexContent === 'undefined' ? "" :
                                    dataDivisionItem.hexContent[0].substring(0, 
                                            dataDivisionItem.hexContent[0].length > 50 ? 50 : dataDivisionItem.hexContent[0].length) }
                                </div>
                            }
                            {typeof dataDivisionItem.showHex === 'undefined' || dataDivisionItem.showHex === false ?  
                                <div className="ToggleHex" 
                                        key={"dataDivisionItemToggleHex" + index}>
                                    <input  type="checkbox" 
                                            id={"checkboxTogglePinnedHexContent" + index} 
                                            onClick={this.toggleHexPinnedDataItem} />
                                </div>
                            :
                                <div className="ToggleHex" 
                                        key={"dataDivisionItemToggleHex" + index}>
                                    <input  type="checkbox" 
                                            id={"checkboxTogglePinnedHexContent" + index} 
                                            onClick={this.toggleHexPinnedDataItem} 
                                            checked />
                                </div>
                            }
                        </div>                                               
                    })}

                    {typeof this.props.report.dataDivisionMap === 'undefined' ? "" :
                     this.props.report.dataDivisionMap.map((dataDivisionItem, index) => {
                        return  <div className="DataDiv" key={"dataDivisionItems" + index}>
                                    <input type="checkbox" id={"checkbox" + index} onClick={this.pinDataItem} />
                                    <div className="DataDivItemName" 
                                         id={"dataDivisionItemName" + index} 
                                         key={"dataDivisionItemName" + index} 
                                         onClick={this.selectUnpinnedDataItem}>
                                        {dataDivisionItem.dataName}
                                    </div>
                                    {typeof this.state.unpinnedHexFlag[index] === 'undefined' || this.state.unpinnedHexFlag[index] === false ?  
                                        <div className="DataDivContent" 
                                             id={"dataDivisionItemEbcdicContent" + index}
                                             key={"dataDivisionItemEbcdicContent" + index}>
                                            {typeof dataDivisionItem.ebcdicContent === 'undefined' ? "" :
                                            dataDivisionItem.ebcdicContent[0].substring(0, 
                                                dataDivisionItem.ebcdicContent[0].length > 50 ? 50 : dataDivisionItem.ebcdicContent[0].length) }
                                        </div>
                                    :
                                        <div className="DataDivHex" 
                                             id={"dataDivisionItemHexContent" + index}
                                             key={"dataDivisionItemHexContent" + index}>
                                            {typeof dataDivisionItem.hexContent === 'undefined' ? "" :
                                            dataDivisionItem.hexContent[0].substring(0, 
                                                dataDivisionItem.hexContent[0].length > 50 ? 50 : dataDivisionItem.hexContent[0].length) }
                                        </div>
                                    }
                                    {typeof this.state.unpinnedHexFlag[index] === 'undefined' || this.state.unpinnedHexFlag[index] === false ?  
                                        <div className="ToggleHex" 
                                             id={"dataDivisionItemToggleHex" + index}
                                             key={"dataDivisionItemToggleHex" + index}>
                                            <input  type="checkbox" 
                                                    id={"checkboxToggleUnpinnedHexContent" + index} 
                                                    onClick={this.toggleHexUnpinnedDataItem} />
                                        </div>
                                    :
                                        <div className="ToggleHex" 
                                             id={"dataDivisionItemToggleHex" + index}
                                             key={"dataDivisionItemToggleHex" + index}>
                                            <input  type="checkbox" 
                                                    id={"checkboxToggleUnpinnedHexContent" + index} 
                                                    onClick={this.toggleHexUnpinnedDataItem} 
                                                    checked />
                                        </div>
                                    }
                    </div>                                            
                    })}
                </div>
                <div class="DataItemDetail">
                    <div class="ContentDetail">
                        <textarea id="contentDetail" class="Textarea" value={this.state.selectedItem} />
                    </div>
                </div>
            </div> 
    );}
}

const mapStateToProps = (state) => {
    return { report : state.report };
}

export default connect(mapStateToProps)(DataDivisionMap);