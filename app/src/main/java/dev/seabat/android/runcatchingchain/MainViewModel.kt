package dev.seabat.android.runcatchingchain

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val TAG = "RUN-CATCHING-CHAIN"

    fun run() {
        badCase()

        case1()
        case2()
        case3()
        case4()
        case5()
        case6()
        case7()
        case8()
        case9()
        case10()
        case11()
        case12()
    }

    private fun badCase() {
        viewModelScope.launch {
            runCatching {
                randomThrows()
            }.onSuccess {
                runCatching {
                    randomThrows()
                }.onSuccess {
                    val result = runCatching {
                        randomThrows()
                    }
                    Log.d(TAG, "[bad case] result: ${result.getOrNull()}")
                }.onFailure {
                    Log.w(TAG, "onFailure: $it.message")
                }
            }.onFailure {
                Log.w(TAG, "onFailure: $it.message")
            }
        }
    }

    /**
     * mapCatching で処理を chain させる
     */
    private fun case1() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.mapCatching {
                it + "2"
            }.mapCatching {
                it + "3"
            }.mapCatching {
                it + "4"
            }

            Log.d(TAG, "[case1] result: ${result.getOrNull()}")
        }
    }

    /**
     * mapCatching で Exception が発生した場合は、 onFailure に入る
     */
    private fun case2() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.mapCatching {
                throw Exception("Exception")
            }.onFailure {
                Log.w(TAG, "onFailure: $it.message")
            }

            Log.d(TAG, "[case2] result: ${result.getOrNull()}")
        }
    }

    /**
     * Exception が発生しない場合は onFailure に入らない
     */
    private fun case3() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.mapCatching {
                it + "2"
            }.onFailure {
                Log.w(TAG, "onFailure: $it.message")
            }

            Log.d(TAG, "[case3] result: ${result.getOrNull()}")
        }
    }

    /**
     * mapCatching で Exception が発生した場合は、 その後の mapCatching をスキップして onFailure に入る
     */
    private fun case4() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.mapCatching {
                throwException()
            }.mapCatching {
                it + "3"
            }.onFailure {
                Log.w(TAG, "onFailure: $it.message")
            }

            Log.d(TAG, "[case4] result: ${result.getOrNull()}")
        }
    }

    /**
     * runCatching で Exception が発生した場合は、その後の mapCatching をスキップして onFailure に入る
     */
    private fun case5() {
        viewModelScope.launch {
            val result = runCatching {
                throwException()
            }.mapCatching {
                it + "2"
            }.onFailure {
                Log.w(TAG, "onFailure: $it.message")
            }

            Log.d(TAG, "[case5] result: ${result.getOrNull()}")
        }
    }

    /**
     * mapCatching が正常終了した場合は onSuccess に入らない
     */
    private fun case6() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.mapCatching {
                it + "2"
            }.onSuccess {
                it + "3"
            }.onFailure {
                Log.w(TAG, "onFailure: ${it.message}")
            }

            Log.d(TAG, "[case6] result: ${result.getOrNull()}")
        }
    }

    /**
     * mapCatching で Exception が発生した場合は、 recoverCatching に入る
     */
    private fun case7() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.mapCatching {
                throw Exception("Exception")
            }.recoverCatching {
                "3"
            }.mapCatching {
                it + "4"
            }.onFailure {
                Log.w(TAG, "onFailure: $it.message")
            }

            Log.d(TAG, "[case7] result: ${result.getOrNull()}")
        }
    }

    /**
     * mapCatching で Exception が発生しない場合は、 recoverCatching に入らない
     */
    private fun case8() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.mapCatching {
                it + "2"
            }.recoverCatching {
                "3"
            }.mapCatching {
                it + "4"
            }

            Log.d(TAG, "[case8] result: ${result.getOrNull()}")
        }
    }

    /**
     * recoverCatching で Exception が発生した場合は、mapCatching に入る
     */
    private fun case9() {
        viewModelScope.launch {
            val result = runCatching {
                throw Exception("Exception")
            }.recoverCatching {
                throwException()
            }.mapCatching {
                it + "3"
            }.onFailure {
                Log.w(TAG, "onFailure ${it.message}")
            }

            Log.d(TAG, "[case9] result: ${result.getOrNull()}")
        }
    }

    /**
     * recoverCatching で Exception が発生した場合は、mapCatching をスキップして recoverCatching に入る
     */
    private fun case10() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.mapCatching {
                throw Exception("Exception")
            }.recoverCatching {
                throwException()
            }.mapCatching {
                it + "4"
            }.recoverCatching {
                "5"
            }

            Log.d(TAG, "[case10] result: ${result.getOrNull()}")
        }
    }

    /**
     * Exception で chain を抜けたい場所に onFailure を配置する
     */
    private fun case11() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.mapCatching {
                throwException()
            }.onFailure {
                Log.w(TAG, "onFailure: ${it.message}")
            }.mapCatching {
                it + "4"
            }

            Log.d(TAG, "[case11] result: ${result.getOrNull()}")
        }
    }

    /**
     * onSuccess で chain させる
     */
    private fun case12() {
        viewModelScope.launch {
            val result = runCatching {
                "1"
            }.onSuccess {
                it + "2"
            }.onSuccess {
                it + "3"
            }

            Log.d(TAG, "[case12] result: ${result.getOrNull()}")
        }
    }

    private fun randomThrows(): String {
        return if ((0..1).random() == 0) {
            throw Exception("Exception")
        } else {
            "R"
        }
    }

    private fun throwException(): String {
        throw Exception("Exception")
    }
}