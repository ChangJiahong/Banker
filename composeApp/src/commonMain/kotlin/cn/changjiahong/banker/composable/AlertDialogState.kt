package cn.changjiahong.banker.composable

class AlertDialogState : DialogState() {
    private var _ok: () -> Unit = {}
    val ok get() = _ok

    fun show(ok: () -> Unit) {
        this._ok = ok
        show()
    }
}