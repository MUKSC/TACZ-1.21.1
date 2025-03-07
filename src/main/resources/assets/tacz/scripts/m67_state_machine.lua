-- 这些栈顶指针在分配新的轨道行和轨道时起作用
-- 轨道行 栈顶指针
local track_line_top = {value = 0}
-- 主轨道行 的 轨道 栈顶指针
local static_track_top = {value = 0}

-- 栈顶指针自增函数，用于分配新的轨道行或者轨道
local function increment(obj)
    obj.value = obj.value + 1
    return obj.value - 1
end

-- 主轨道行
local STATIC_TRACK_LINE = increment(track_line_top)
local BASE_TRACK = increment(static_track_top)
local MAIN_TRACK = increment(static_track_top)

-- 播放丢枪动画的方法
local function runPutAwayAnimation(context)
    local put_away_time = context:getPutAwayTime()
    -- 此处获取的轨道是位于主轨道行上的主轨道
    local track = context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK)
    -- 播放 put_away 动画,并且将其过渡时长设为从上下文里传入的 put_away_time * 0.75
    context:runAnimation("put_away", track, false, PLAY_ONCE_HOLD, put_away_time * 0.75)
    -- 设定动画进度为最后一帧
    context:setAnimationProgress(track, 1, true)
    -- 将动画进度向前拨动 {put_away_time}
    context:adjustAnimationProgress(track, -put_away_time, false)
end

local main_track_states = {
    -- 起始
    start = {},
    -- 闲置
    idle = {},
    -- 使用
    using = {},
    -- loop
    using_hold = {},
    -- 衔接
    after_use = {},
    -- 检视
    inspect = {},
    -- 最终态
    final = {},
    lastCount = -1
}

local base_track_state = {}
-- 进入基础状态,直接播放 static_idle
function base_track_state.entry(this, context)
    -- 在 主轨道行 的 基础轨道 上循环播放 static_idle
    context:runAnimation("static_idle", context:getTrack(STATIC_TRACK_LINE, BASE_TRACK), false, LOOP, 0)
end

function main_track_states.start.update(this, context)
    context:trigger(INPUT_DRAW)
end

function main_track_states.start.transition(this, context, input)
    if (input == INPUT_DRAW) then
        context:runAnimation("draw", context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK), false, PLAY_ONCE_STOP, 0)
        return this.main_track_states.idle
    end
end

function main_track_states.idle.entry(this, context)
    print("idle entry")
end

function main_track_states.idle.update(this, context)
    if context:isUsing() then
        context:trigger("start_use")
    end
end

function main_track_states.idle.transition(this, context, input)
    print("idle entry - " .. input)
    if (input == INPUT_PUT_AWAY) then
        runPutAwayAnimation(context)
        -- 丢枪后转到最终态
        return this.main_track_states.final
    elseif (input == INPUT_INSPECT) then
        context:runAnimation("inspect", context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK), false, PLAY_ONCE_STOP, 0.2)
        return this.main_track_states.idle
    elseif (input == "start_use") then
        context:runAnimation("unlock_safe", context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK), false, PLAY_ONCE_HOLD, 0)
        return this.main_track_states.using
    end
end

function main_track_states.using.entry(this, context)
    print("using entry")
end

function main_track_states.using.update(this, context)
    if not context:isUsing() then
        context:trigger("idle")
    elseif context:getUsingTick() >= 22 then
        context:trigger("using_hold")
    end
end

function main_track_states.using.transition(this, context, input)
    if (input == INPUT_PUT_AWAY) then
        runPutAwayAnimation(context)
        -- 丢枪后转到最终态
        return this.main_track_states.final
    elseif (input == "idle") then
        context:pauseAnimation(context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK))
        context:setAnimationProgress(context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK), 0, true)
        return this.main_track_states.idle
    elseif (input == "using_hold") then
        context:runAnimation("unlock_safe_loop", context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK), false, LOOP, 0.1)
        return this.main_track_states.using_hold
    end
end

function main_track_states.using_hold.update(this, context)
    if not context:isUsing() then
        context:trigger("throw")
    end
end

function main_track_states.using_hold.transition(this, context, input)
    if (input == INPUT_PUT_AWAY) then
        runPutAwayAnimation(context)
        -- 丢枪后转到最终态
        return this.main_track_states.final
    elseif (input == "throw") then
        local track = context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK)
        context:stopAnimation(track)
        context:runAnimation("throw", track, false, PLAY_ONCE_HOLD, 0.01)
        return this.main_track_states.after_use
    end
end

function main_track_states.after_use.update(this, context)
    local track = context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK)
    if (context:isHolding(track)) then
        context:trigger("end_throw")
    end
end

function main_track_states.after_use.transition(this, context, input)
    if (input == INPUT_PUT_AWAY) then
        runPutAwayAnimation(context)
        -- 丢枪后转到最终态
        return this.main_track_states.final
    elseif (input == "end_throw") then
        -- 还有剩余物品，触发重新切入动画并返回idle
        if context:getStackCount() > 0 then
            context:runAnimation("re_draw", context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK), false, PLAY_ONCE_STOP, 0)
            return this.main_track_states.idle
        end
    end
end

local M = {
    track_line_top = track_line_top,
    STATIC_TRACK_LINE = STATIC_TRACK_LINE,

    base_track_state = base_track_state,
    main_track_states = main_track_states,
}

-- 状态机初始化函数，在切入物品的时候调用
function M:initialize(context)
    context:ensureTrackLineSize(track_line_top.value)
    context:ensureTracksAmount(STATIC_TRACK_LINE, static_track_top.value)
    main_track_states.lastCount = context:getStackCount()
end

-- 状态机退出函数，在切出物品的时候调用
function M:exit(context)
    main_track_states.lastCount = -1
end

function M:states()
    return {
        self.base_track_state,
        self.main_track_states.start
    }
end

return M