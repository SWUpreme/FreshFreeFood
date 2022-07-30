package com.example.fffroject

import android.graphics.Canvas
import android.icu.lang.UCharacter
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

// 스와이프 삭제를 위한 클래스 추가
class SwipeHelperCallback : ItemTouchHelper.Callback() {

//    private var currentPosition: Int? = null
//    private var previousPosition: Int? = null
//    private var currentDx = 0f
//    private var clamp = 0f
//
//    override fun getMovementFlags(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder
//    ): Int {
//        // Drag와 Swipe 방향을 결정 Drag는 사용하지 않아 0, Swipe의 경우는 LEFT, RIGHT 모두 사용가능하도록 설정
//        return makeMovementFlags(0, UCharacter.IndicPositionalCategory.LEFT or UCharacter.IndicPositionalCategory.RIGHT)
//    }
//
//    override fun onMove(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ) = false
//
//    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//        currentDx = 0f
//        previousPosition = viewHolder.adapterPosition
//        getDefaultUIUtil().clearView(getView(viewHolder))
//    }
//
//    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//        viewHolder?.let {
//            currentPosition = viewHolder.adapterPosition
//            getDefaultUIUtil().onSelected(getView(it))
//        }
//    }
//
//    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
//        val isClamped = getTag(viewHolder)
//        // 현재 View가 고정되어있지 않고 사용자가 -clamp 이상 swipe시 isClamped true로 변경 아닐시 false로 변경
//        setTag(viewHolder, !isClamped && currentDx <= -clamp)
//        return 2f
//    }
//
//    override fun onChildDraw(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        dX: Float,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
//    ) {
//
//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//            val view = getView(viewHolder)
//            val isClamped = getTag(viewHolder)
//            val x =  clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)
//
//            currentDx = x
//            getDefaultUIUtil().onDraw(
//                c,
//                recyclerView,
//                view,
//                x,
//                dY,
//                actionState,
//                isCurrentlyActive
//            )
//        }
//    }
//
//    private fun getView(viewHolder: RecyclerView.ViewHolder) : View = viewHolder.itemView.findViewById(R.id.swipe_ex)
//
//    // 사용자가 view를 swipe 했다고 간주할 최소 속도 정하기
//    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = defaultValue * 20
//
//    // 사용자가 view를 swipe 했다고 간주하기 위해 이동해야하는 부분 반환
//    // (사용자가 손을 떼면 호출됨)
////    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
////        // -clamp 이상 swipe시 isClamped를 true로 변경 아닐시 false로 변경
////        setTag(viewHolder, currentDx <= -clamp)
////        return 2f
////    }
//
//
//
//    private fun clampViewPositionHorizontal(
//        view: View,
//        dX: Float,
//        isClamped: Boolean,
//        isCurrentlyActive: Boolean
//    ) : Float {
//        // View의 가로 길이의 절반까지만 swipe 되도록
//        val min: Float = -view.width.toFloat()/3
//        // RIGHT 방향으로 swipe 막기
//        val max = 0f
//
//        // 고정할 수 있으면
//        val newX = if (isClamped) {
//            // 현재 swipe 중이면 swipe되는 영역 제한
//            if (isCurrentlyActive)
//            // 오른쪽 swipe일 때
//                if (dX < 0) dX/3 - clamp
//                // 왼쪽 swipe일 때
//                else dX - clamp
//            // swipe 중이 아니면 고정시키기
//            else -clamp
//        }
//        // 고정할 수 없으면 newX는 스와이프한 만큼
//        else dX / 2
//
//        // newX가 0보다 작은지 확인
//        return min(newX, max)
//    }
//
//    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
//        // isClamped를 view의 tag로 관리
//        viewHolder.itemView.tag = isClamped
//    }
//
//    private fun getTag(viewHolder: RecyclerView.ViewHolder) : Boolean {
//        // isClamped를 view의 tag로 관리
//        return viewHolder.itemView.tag as? Boolean ?: false
//    }
//
//    fun setClamp(clamp: Float) {
//        this.clamp = clamp
//    }
//
//    // 다른 View가 swipe 되거나 터치되면 고정 해제
//    fun removePreviousClamp(recyclerView: RecyclerView) {
//        if (currentPosition == previousPosition)
//            return
//        previousPosition?.let {
//            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
//            getView(viewHolder).translationX = 0f
//            setTag(viewHolder, false)
//            previousPosition = null
//        }
//    }
//
//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    override fun getMovementFlags(  // 이동 방향을 결정!!
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // Drag와 Swipe 방향을 결정 Drag는 사용하지 않아 0, Swipe의 경우는 오른쪽에서 왼쪽으로만 가능하게 설정,   양방향 모두 하고 싶다면 'ItemTouchHelper.LEFT or ItemTouchHelper.Right'
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(  // Drag 시 호출
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {  // Swipe 시 호출

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        //super.clearView(recyclerView, viewHolder)
        currentDx = 0f
        previousPosition = viewHolder.adapterPosition
        getDefaultUIUtil().clearView(getView(viewHolder))
    }


    // Called when the ViewHolder swiped or dragged by the ItemTouchHelper is changed =
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        //super.onSelectedChanged(viewHolder, actionState)
        viewHolder?.let {
            currentPosition = viewHolder.adapterPosition
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val isClamped = getTag(viewHolder)
        // 현재 View가 고정되어있지 않고 사용자가 -clamp 이상 swipe시 isClamped true로 변경 아닐시 false로 변경
        setTag(viewHolder, !isClamped && currentDx <= -clamp)
        Log.d("isClamped 는 ", isClamped.toString())
        return 2f
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if(actionState == ACTION_STATE_SWIPE){
            val view = getView(viewHolder)
            val isClamped = getTag(viewHolder)
            val x =  clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)

            currentDx = x
            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                x,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ) : Float {
        // RIGHT 방향으로 swipe 막기
        val max = 0f

        // 고정할 수 있으면
        val newX = if (isClamped) {
            // 현재 swipe 중이면 swipe되는 영역 제한
            if (isCurrentlyActive)
            // 오른쪽 swipe일 때
                if (dX < 0) dX/3 - clamp
                // 왼쪽 swipe일 때
                else dX - clamp
            // swipe 중이 아니면 고정시키기
            else -clamp
        }
        // 고정할 수 없으면 newX는 스와이프한 만큼
        else dX / 2

        // newX가 0보다 작은지 확인
        return min(newX, max)
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder) :
            View = viewHolder.itemView.findViewById(R.id.swipe_view) // 아이템뷰에서 스와이프 영역에 해당하는 뷰 가져오기

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        // isClamped를 view의 tag로 관리
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder) : Boolean =  viewHolder.itemView.tag as? Boolean ?: false

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    // 다른 View가 swipe 되거나 터치되면 고정 해제
    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition)
            return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).translationX = 0f
            setTag(viewHolder, false)
            previousPosition = null
        }
    }
}