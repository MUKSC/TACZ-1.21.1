-- 脚本的位置是 "{命名空间}:{路径}"，那么 require 的格式为 "{命名空间}_{路径}"
-- 注意！require 取得的内容不应该被修改，应仅调用
local default = require("tacz_default_state_machine")
local GUN_KICK_TRACK_LINE = default.GUN_KICK_TRACK_LINE
local gun_kick_state = default.gun_kick_state
-- main_track_states.idle 是我们要重写的状态。
local shoot_state = setmetatable({}, {__index = gun_kick_state})
-- reload_state、bolt_state 是定义的新状态，用于执行单发装填

function shoot_state.transition(this, context, input)
    -- 玩家按下开火键时需要在射击轨道行里寻找空闲轨道去播放射击动画(如果没有空闲会分配新的),需要注意的是射击动画要向下混合
    if (input == INPUT_SHOOT) then
        local track = context:findIdleTrack(GUN_KICK_TRACK_LINE, false)
        -- 这里是混合动画，一般是可叠加的 gun kick
        if (context:getFireMode() == BURST) then
            context:runAnimation("shoot_burst", track, true, PLAY_ONCE_STOP, 0)
        else
            context:runAnimation("shoot", track, true, PLAY_ONCE_STOP, 0)
        end
    end
    return nil
end

-- 用元表的方式继承默认状态机的属性
local M = setmetatable({
    gun_kick_state = shoot_state
}, {__index = default})
-- 先调用父级状态机的初始化函数，然后进行自己的初始化
function M:initialize(context)
    default.initialize(self, context)
end
-- 导出状态机
return M