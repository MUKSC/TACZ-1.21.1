local M = {}

function M.shoot(api)
    if (api:getFireMode() == BURST) then
        api:shootOnce(api:isShootingNeedConsumeAmmo())
        api:shootOnce(api:isShootingNeedConsumeAmmo())
    else
        api:shootOnce(api:isShootingNeedConsumeAmmo())
    end
end

return M