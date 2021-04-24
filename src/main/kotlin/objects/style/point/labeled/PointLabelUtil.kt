package objects.style.point.labeled

val String.mathMode: String
    get() {
        return if ('_' in this)
            this.substringBefore('_') + "\\(_{${this.substringAfter('_')}}\\)"
        else this
    }