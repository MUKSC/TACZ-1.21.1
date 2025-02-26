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

local main_track_states = {
    -- 起始
    start = {},
    -- 闲置
    idle = {},
    -- 使用
    using = {},
    -- loop
    using_hold = {},
    -- 最终态
    final = {}
}

local base_track_state = {}
-- 进入基础状态,直接播放 static_idle
function base_track_state.entry(this, context)
    -- 在 主轨道行 的 基础轨道 上循环播放 static_idle
    context:runAnimation("static_idle", context:getTrack(STATIC_TRACK_LINE, BASE_TRACK), false, LOOP, 0)
end

function main_track_states.start.update(this, context)
    context:trigger("draw")
end

function main_track_states.start.transition(this, context, input)
    context:runAnimation("draw", context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK), false, PLAY_ONCE_STOP, 0)
    return this.main_track_states.idle
end

function main_track_states.idle.update(this, context)
    if context:isUsing() then
        context:trigger("start_use")
    end
end

function main_track_states.idle.transition(this, context, input)
    print("idle transition - " .. input)
    if (input == "start_use") then
        context:runAnimation("unlock_safe", context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK), false, PLAY_ONCE_STOP, 0)
        return this.main_track_states.using
    end
end

function main_track_states.using.update(this, context)
    print("using update")
    if not context:isUsing() then
        context:trigger("idle")
    elseif context:getUsingTick() >= 10 then
        context:trigger("using_hold")
    end
end

function main_track_states.using.transition(this, context, input)
    print("using transition - " .. input)
    if (input == "idle") then
        -- 立刻停止动画
        context:stopAnimation(context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK))
        return this.main_track_states.idle
    elseif (input == "using_hold") then
        context:runAnimation("unlock_safe_loop", context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK), false, LOOP, 0)
        return this.main_track_states.using_hold
    end
end

function main_track_states.using_hold.update(this, context)
    print("using_hold update")
    if not context:isUsing() then
        context:trigger("throw")
    end
end

function main_track_states.using_hold.transition(this, context, input)
    print("using_hold transition " .. input)
    if (input == "throw") then
        local track = context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK)
        context:stopAnimation(track)
        context:runAnimation("throw", track, false, PLAY_ONCE_STOP, 0.01)
        return this.main_track_states.idle
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
end

-- 状态机退出函数，在切出物品的时候调用
function M:exit(context)
    -- do some cleaning up things
end

function M:states()
    return {
        self.base_track_state,
        self.main_track_states.idle
    }
end

return M