local npc = Npc(97, 9046)

npc.barters = {
    {to={itemID=433, quantity= 1}, from= {
        {itemID=394, quantity= 80},
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(300, {242, 237})
    elseif answer == 242 then p:ask(307)
    elseif answer == 237 then p:ask(302, {244, 238})
    elseif answer == 244 then p:ask(307)
    elseif answer == 238 then p:ask(303, {248, 245})
    elseif answer == 245 then p:ask(307)
    elseif answer == 248 then p:ask(304, {240, 246})
    elseif answer == 240 then p:ask(305, {247, 249})
    elseif answer == 247 then p:ask(307)
    elseif answer == 249 then p:ask(306)
    elseif answer == 246 then p:ask(307)
    end
end

RegisterNPCDef(npc)
