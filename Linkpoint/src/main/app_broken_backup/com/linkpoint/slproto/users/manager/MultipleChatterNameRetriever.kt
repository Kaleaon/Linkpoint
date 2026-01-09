package com.linkpoint.slproto.users.manager

class MultipleChatterNameRetriever {
    interface OnChatterNameUpdated {
        fun onChatterNameUpdated(retriever: MultipleChatterNameRetriever)
    }
}
