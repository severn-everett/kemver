package com.severett.kemver.processor

class RangeProcessorPipeline(processors: List<Processor>) {
    private val processors = processors.toMutableList()

    fun addProcessor(processor: Processor) = this.apply { processors.add(processor) }

    fun process(range: String) = processors.fold(range) { acc, processor -> processor.process(acc) }

    companion object {
        @JvmStatic
        fun startWith(processor: Processor) = RangeProcessorPipeline(listOf(processor))

        @JvmStatic
        fun build(builder: RangeProcessorPipeline.() -> Unit) = RangeProcessorPipeline(emptyList()).apply(builder)
    }
}
