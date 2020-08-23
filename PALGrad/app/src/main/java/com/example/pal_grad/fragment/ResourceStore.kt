package com.example.pal_grad.fragment

interface ResourceStore {
    companion object {
        val tabList = listOf(
            "페이스 체인지", "애니메이션", "소개"
        )
        val pagerFragments = listOf(
            HomeFragment.create(), BestSellerFragment.create(),
            NewBookFragment.create(), MyFragment.create())
    }
}