-- 脚本的位置是 "{命名空间}:{路径}"，那么 require 的格式为 "{命名空间}_{路径}"
-- 注意！require 取得的内容不应该被修改，应仅调用
local default = require("tacz_default_state_machine")
local STATIC_TRACK_LINE = default.STATIC_TRACK_LINE
local BLENDING_TRACK_LINE = default.BLENDING_TRACK_LINE
local BOLT_CAUGHT_TRACK = default.BOLT_CAUGHT_TRACK
local BASE_TRACK = default.BASE_TRACK
local SLIDE_TRACK = default.SLIDE_TRACK
local MAIN_TRACK = default.MAIN_TRACK
local bolt_caught_states = default.bolt_caught_states

local normal_state = setmetatable({}, {__index = bolt_caught_states.normal})

-- 检查当前是否还有弹药
local function isNoAmmo(context)
    -- 这里同时检查了枪管和弹匣
    return (not context:hasBulletInBarrel()) and (context:getAmmoCount() <= 0)
end

-- 更新"不空挂"状态
function normal_state.update(this, context)
    -- 如果弹药数量是 0 了,那么立刻手动触发一次转到"空挂"状态的输入
    if (isNoAmmo(context)) then
        context:stopAnimation(context:getTrack(STATIC_TRACK_LINE, BOLT_CAUGHT_TRACK))
        context:trigger(this.INPUT_BOLT_CAUGHT)
    else
        local a = context:getAmmoCount()
        if (a < 9) then
            context:setAnimationProgress(context:getTrack(STATIC_TRACK_LINE, BOLT_CAUGHT_TRACK),0.1+(8-a)*0.5,false)
        else
            context:setAnimationProgress(context:getTrack(STATIC_TRACK_LINE, BOLT_CAUGHT_TRACK),0.1,false)
        end
    end
end

-- 进入"不空挂"状态
function normal_state.entry(this, context)
    context:runAnimation("static_ammo_display", context:getTrack(STATIC_TRACK_LINE, BOLT_CAUGHT_TRACK), false, PLAY_ONCE_STOP, 0)
    this.bolt_caught_states.normal.update(this, context)
end

local crawl_states = {
    draw = {},
    normal = {},
    crawl = {},
    played_animation = 0
}

function crawl_states.normal.transition(this, context, input)
    -- 趴下时切到趴下状态
    if (context:isCrawl()) then
        return this.crawl_states.crawl
    end
end

function crawl_states.crawl.entry(this, context)
    -- 重置主轨道动画标志位
    crawl_states.played_animation = 0
end

function crawl_states.crawl.update(this, context)
    -- 主轨道正在播放动画 且 趴下轨道无动画 时播放脚架单独展开
    if ((not context:isStopped(context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK))) and context:isStopped(context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK))) then
        context:runAnimation("crawl_bipod", context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK), true, PLAY_ONCE_HOLD, 0.5)
        crawl_states.played_animation = 1
    end
    -- 主轨道无动画 且 趴下轨道无动画 时播放趴下的起手式
    if (context:isStopped(context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK)) and context:isStopped(context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK))) then
        context:runAnimation("crawl_start", context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK), true, PLAY_ONCE_HOLD, 0.5)
    end
    -- 主轨道无动画 且 趴下轨道被挂起（其实就是起手式播放完了） 时播放趴下的持续动作
    if (context:isStopped(context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK)) and context:isHolding(context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK))) then
        -- 主轨道没有播放过动画 持续播放趴下动作
        if (crawl_states.played_animation == 0) then
            context:runAnimation("crawl", context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK), true, PLAY_ONCE_HOLD, 0.4)
        --主轨道播放过动画 用单独的手臂归为动画回到趴下状态并重置标志位
        elseif (crawl_states.played_animation == 1) then
            context:runAnimation("crawl_handup", context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK), true, PLAY_ONCE_HOLD, 0.4)
            crawl_states.played_animation = 0
        end
    end
    -- 主轨道正在播放动画 且 趴下轨道被挂起 时将趴下的叠加层清除掉并将标志位置1
    if ((not context:isStopped(context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK))) and context:isHolding(context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK))) then
        if (crawl_states.played_animation == 0) then
            context:runAnimation("crawl_handdown", context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK), true, PLAY_ONCE_HOLD, 0.4)
            crawl_states.played_animation = 1
        end
    end
end

function crawl_states.crawl.transition(this, context, input)
    if(not context:isCrawl() or this.main_track_states.final.isfinal == 1) then
        if (not context:isStopped(context:getTrack(STATIC_TRACK_LINE, MAIN_TRACK))) then
            context:runAnimation("crawl_bipod_end", context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK), true, PLAY_ONCE_STOP, 0.2)
        else
            context:runAnimation("crawl_end", context:getTrack(BLENDING_TRACK_LINE, SLIDE_TRACK), true, PLAY_ONCE_STOP, 0.2)
        end
        print("exit")
        return this.crawl_states.normal
    end
end

-- 用元表的方式继承默认状态机的属性
local M = setmetatable({
    bolt_caught_states = setmetatable({
        normal = normal_state,
    }, {__index = bolt_caught_states}),
    crawl_states = crawl_states
}, {__index = default})
function M:initialize(context)
    default.initialize(self, context)
end
-- 继承默认状态机需要重新初始化状态
function M:states()
    return {
        self.base_track_state,
        self.bolt_caught_states.normal,
        self.over_heat_states.normal,
        self.main_track_states.start,
        self.gun_kick_state,
        self.movement_track_states.idle,
        self.ADS_states.normal,
        self.slide_states.normal,
        self.crawl_states.normal
    }
end
-- 导出状态机
return M