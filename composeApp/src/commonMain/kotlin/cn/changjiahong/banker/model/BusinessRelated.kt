package cn.changjiahong.banker.model

class BusinessRelated(val flag: Long) {

    companion object {
        val EPay = BusinessRelated(1L shl 0)

    }


}


infix fun BusinessRelated.are(br: BusinessRelated): Boolean {
    return this.flag and br.flag == br.flag
}