package com.trace.gtrack.ui.assignqr.common

interface IRFIDReaderListener {
    fun newTagRead(epc : String?)
}