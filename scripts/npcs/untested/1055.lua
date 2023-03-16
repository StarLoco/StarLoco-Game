local npc = Npc(1055, 121)

npc.gender = 1
npc.accessories = {0, 2411, 2414, 35, 9025}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5775, {4982, 4983})
    elseif answer == 4983 then p:ask(5776, {2812})
    end
end

RegisterNPCDef(npc)
