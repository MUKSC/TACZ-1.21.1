local M = {}

-- 自定义的散步函数，返回一对坐标{x, y}，代表子弹以准心为原点，平行于屏幕平面8格外的落点
-- 下面是一个参考，子弹将以一个半径为1的圆形均匀分布
function M.calcSpread(api, ammoCnt, basicInaccuracy)
    local angle = (ammoCnt / 10) * 2 * math.pi
    return {math.cos(angle), math.sin(angle)}
end

return M