local M = {}

local function tick_normal(api, heatTimestamp)
    local delay = api:getCoolingDelay()
    local now = api:getCurrentTimestamp()

    if now - heatTimestamp >= delay then
        local heat = api:getHeatAmount() - api:calcHeatReduction(heatTimestamp)
        api:setHeatAmount(heat)
    end
end

local function tick_locked(api, heatTimestamp)
    local overheatTime = api:getOverheatTime()
    local now = api:getCurrentTimestamp()

    if now - heatTimestamp >= overheatTime then
        local heat = api:getHeatAmount() - api:calcHeatReduction(heatTimestamp)
        api:setHeatAmount(heat)
        if heat <= 0 then
            api:setOverheatLocked(false);
        end
    end
end

function M.tick_heat(api, heatTimestamp)
    if api:hasHeatData() then
        if api:isOverheatLocked() then
            tick_locked(api, heatTimestamp)
        else
            tick_normal(api, heatTimestamp)
        end
    end
end

-- 警告，此方法是shootOnce的一部分，不要在此处尝试射击，不然会死循环
function M.handle_shoot_heat(api)
    if api:hasHeatData() then
        local heatMax = api:getHeatMax()
        local heat = math.min(api:getHeatAmount() + api:getHeatPerShot(), heatMax)
        api:setHeatAmount(heat)
        if heat >= heatMax then
            api:setOverheatLocked(true);
        end
    end
end

return M