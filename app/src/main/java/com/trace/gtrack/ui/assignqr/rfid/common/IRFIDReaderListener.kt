package com.trace.gtrack.ui.assignqr.rfid.common

interface IRFIDReaderListener {
    fun newTagRead(epc : String?)
}