-- 定义逻辑机?��定�?
local M = {}

-- 当开始换弹�?时候会�?用一次
function M.start_reload(api)
    return true
end

-- 这是个 lua 函数?��用来从枪 data �?件里获取�?弹相关�?动画时间点?��由�? lua �?�?时间是毫秒，所以要和 1000 做乘�?
local function getReloadTimingFromParam(param)
    local reload_feed = {param.reload_feed, param.reload_xmag_1_feed, param.reload_xmag_2_feed, param.reload_xmag_3_feed}
    local reload_cooldown = {param.reload_cooldown, param.reload_xmag_1_cooldown, param.reload_xmag_2_cooldown, param.reload_xmag_3_cooldown}
    local empty_feed = {param.empty_feed, param.empty_xmag_1_feed, param.empty_xmag_2_feed, param.empty_xmag_3_feed}
    local empty_cooldown = {param.empty_cooldown, param.empty_xmag_1_cooldown, param.empty_xmag_2_cooldown, param.empty_xmag_3_cooldown}
    for i = 1, 4 do
        -- �? param 中�?时间点转换为毫�?
        -- 如果有nil直接返回nil
        if (reload_feed[i] == nil or reload_cooldown[i] == nil or empty_feed[i] == nil or empty_cooldown[i] == nil) then
            return nil, nil, nil, nil
        end
        reload_feed[i] = reload_feed[i] * 1000
        reload_cooldown[i] = reload_cooldown[i] * 1000
        empty_feed[i] = empty_feed[i] * 1000
        empty_cooldown[i] = empty_cooldown[i] * 1000
    end

    -- 顺序返回获取到�?�? 4 个数�?
    return reload_feed, reload_cooldown, empty_feed, empty_cooldown
end

-- 判断这个状态是否是空仓换弹�?程中�?其中一个阶段。包括空仓换弹�?收尾阶段
local function isReloadingEmpty(stateType)
    return stateType == EMPTY_RELOAD_FEEDING or stateType == EMPTY_RELOAD_FINISHING
end

-- 判断这个状态是否是战术换弹�?程中�?其中一个阶段。包括战术换弹�?收尾阶段
local function isReloadingTactical(stateType)
    return stateType == TACTICAL_RELOAD_FEEDING or stateType == TACTICAL_RELOAD_FINISHING
end

-- 判断这个状态是否是任意换弹�?程中�?其中一个阶段。包括任意换弹�?收尾阶段
local function isReloading(stateType)
    return isReloadingEmpty(stateType) or isReloadingTactical(stateType)
end

-- 判断这个状态是否是任意换弹�?程中�?�?收尾阶段
local function isReloadFinishing(stateType)
    return stateType == EMPTY_RELOAD_FINISHING or stateType == TACTICAL_RELOAD_FINISHING
end

local function finishReload(api, is_tactical)
    local needAmmoCount = api:getNeededAmmoAmount();
    if (api:isReloadingNeedConsumeAmmo()) then
        -- 需要消耗弹药?��生存�?��?�险?��的话就消耗换弹所需�?弹药并�?消耗的数量�?填进弹匣
        api:putAmmoInMagazine(api:consumeAmmoFromPlayer(needAmmoCount))
    else
        -- 不需要消耗弹药?���?��??��的话就直接把弹匣塞满
        api:putAmmoInMagazine(needAmmoCount)
    end
    if not is_tactical then
        local i = api:removeAmmoFromMagazine(1);
        if i ~= 0 then
            api:setAmmoInBarrel(true)
        end
    end
end

function M.tick_reload(api)
    -- 从枪 data �?件中获取所有需要�?入逻辑机�?参数?��注意此时�? param 是个列表?��还不�?�直接拿来用
    local param = api:getScriptParams();
    -- �?用刚才�? lua 函数?��把 param 里包含�?八个参数依次赋值给�?�们新定义的变量
    local reload_feed, reload_cooldown, empty_feed, empty_cooldown = getReloadTimingFromParam(param)
    -- 照例检查是否有参数缺失
    if (reload_feed == nil or reload_cooldown == nil or empty_feed == nil or empty_cooldown == nil) then
        return NOT_RELOADING, -1
    end

    -- 获取当前弹匣等级?���?�们�?设最�? 3 级
    local mag_level = math.min(api:getMagExtentLevel(), 3) + 1

    local countDown = -1
    local stateType = NOT_RELOADING
    local oldStateType = api:getReloadStateType()

    -- 获取换弹时间?��在玩家按�? R �?一瞬间作为零点?��单位是毫秒。假设玩家在一秒前按下�? R ?��那么此时这个时间就是 1000
    local progressTime = api:getReloadTime()

    if isReloadingEmpty(oldStateType) then
        local feed_time = empty_feed[mag_level]
        local finishing_time = empty_cooldown[mag_level]
        if progressTime < feed_time then
            stateType = EMPTY_RELOAD_FEEDING
            countDown = feed_time - progressTime
        elseif progressTime < finishing_time then
            stateType = EMPTY_RELOAD_FINISHING
            countDown = finishing_time - progressTime
        else
            stateType = NOT_RELOADING;
            countDown = -1
        end
    elseif isReloadingTactical(oldStateType) then
        local feed_time = reload_feed[mag_level]
        local finishing_time = reload_cooldown[mag_level]
        if progressTime < feed_time then
            stateType = TACTICAL_RELOAD_FEEDING
            countDown = feed_time - progressTime
        elseif progressTime < finishing_time then
            stateType = TACTICAL_RELOAD_FINISHING
            countDown = finishing_time - progressTime
        else
            stateType = NOT_RELOADING;
            countDown = -1
        end
    else
        stateType = NOT_RELOADING;
        countDown = -1
    end

    if oldStateType == EMPTY_RELOAD_FEEDING and oldStateType ~= stateType then
        finishReload(api,false);
    end

    if oldStateType == TACTICAL_RELOAD_FEEDING and oldStateType ~= stateType then
        finishReload(api, true);
    end

    return stateType, countDown
end

-- 向模�?返回整个逻辑机?��定�?
return M